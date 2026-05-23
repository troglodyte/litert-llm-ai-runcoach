package com.litert.coach.ai

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ModelDownloader"

sealed class DownloadState {
    data object Idle : DownloadState()
    data class Downloading(val progressPercent: Int, val downloadedMb: Float, val totalMb: Float) : DownloadState()
    data object Verifying : DownloadState()
    data object Complete : DownloadState()
    data class Failed(val message: String) : DownloadState()
}

@Singleton
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    fun download(variant: ModelVariant, hfToken: String? = null): Flow<DownloadState> = channelFlow {
        val config = ModelConfig.configs[variant]
            ?: run {
                Log.e(TAG, "No config for variant: $variant")
                send(DownloadState.Failed("Unknown variant: $variant"))
                return@channelFlow
            }

        val modelDir = File(context.filesDir, ModelConfig.MODEL_DIR).also { it.mkdirs() }
        val destFile = File(modelDir, config.fileName)
        val tempFile = File(modelDir, "${config.fileName}.tmp")

        Log.d(TAG, "Download start: ${config.fileName}")
        Log.d(TAG, "  URL: ${config.downloadUrl}")
        Log.d(TAG, "  Dest: ${destFile.absolutePath}")
        Log.d(TAG, "  Token provided: ${hfToken != null}")

        send(DownloadState.Downloading(0, 0f, 0f))

        val requestBuilder = Request.Builder().url(config.downloadUrl)
        if (!hfToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $hfToken")
        }
        val request = requestBuilder.build()

        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Executing HTTP request...")
                okHttpClient.newCall(request).execute().use { response ->
                    Log.d(TAG, "HTTP ${response.code} ${response.message} from ${response.request.url}")
                    Log.d(TAG, "=== Response Headers ===")
                    response.headers.forEach { (name, value) ->
                        Log.d(TAG, "  $name: $value")
                    }
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "(empty body)"
                        Log.e(TAG, "=== Response Body ===")
                        Log.e(TAG, errorBody)
                        val msg = when (response.code) {
                            401 -> "HTTP 401: Unauthorized. Check your Hugging Face token."
                            403 -> "HTTP 403: Forbidden. Accept the Gemma license at huggingface.co/google/gemma-3n-E1B-it-litert-lm"
                            404 -> "HTTP 404: Not Found. The model is gated — accept the license at huggingface.co and ensure your token has access.\n\nServer: $errorBody"
                            else -> "HTTP ${response.code} ${response.message}: $errorBody"
                        }
                        Log.e(TAG, "Download failed: $msg")
                        send(DownloadState.Failed(msg))
                        return@withContext
                    }
                    val body = response.body ?: run {
                        Log.e(TAG, "Response body is null")
                        send(DownloadState.Failed("Empty response body"))
                        return@withContext
                    }
                    val totalBytes = body.contentLength()
                    Log.d(TAG, "Content-Length: $totalBytes bytes (${totalBytes / 1_048_576} MB)")
                    var downloadedBytes = 0L
                    val buffer = ByteArray(8192)
                    var lastLoggedPercent = -1

                    tempFile.outputStream().use { out ->
                        body.byteStream().use { input ->
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                out.write(buffer, 0, read)
                                downloadedBytes += read
                                if (totalBytes > 0) {
                                    val progress = (downloadedBytes * 100 / totalBytes).toInt()
                                    if (progress / 10 > lastLoggedPercent / 10) {
                                        Log.d(TAG, "Progress: $progress% (${downloadedBytes / 1_048_576} / ${totalBytes / 1_048_576} MB)")
                                        lastLoggedPercent = progress
                                    }
                                    send(
                                        DownloadState.Downloading(
                                            progressPercent = progress,
                                            downloadedMb = downloadedBytes / 1_048_576f,
                                            totalMb = totalBytes / 1_048_576f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (totalBytes > 0 && downloadedBytes < totalBytes) {
                        val msg = "Download incomplete: $downloadedBytes / $totalBytes bytes"
                        Log.e(TAG, msg)
                        send(DownloadState.Failed(msg))
                        return@withContext
                    }

                    Log.d(TAG, "Download complete (${downloadedBytes / 1_048_576} MB). Moving to $destFile...")
                    if (tempFile.renameTo(destFile)) {
                        Log.d(TAG, "File moved successfully.")
                        send(DownloadState.Complete)
                    } else {
                        val msg = "Failed to rename ${tempFile.name} → ${destFile.name}"
                        Log.e(TAG, msg)
                        send(DownloadState.Failed(msg))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error during download: ${e.message}", e)
                send(DownloadState.Failed("Network error: ${e.localizedMessage}"))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun isDownloaded(variant: ModelVariant): Boolean {
        val config = ModelConfig.configs[variant] ?: return false
        return File(context.filesDir, "${ModelConfig.MODEL_DIR}/${config.fileName}").exists()
    }
}
