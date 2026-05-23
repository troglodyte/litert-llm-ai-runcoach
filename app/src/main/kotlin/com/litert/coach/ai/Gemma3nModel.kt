package com.litert.coach.ai

import android.content.Context
import android.util.Log
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "Gemma3nModel"

@Singleton
class Gemma3nModel @Inject constructor(
    @ApplicationContext private val context: Context
) : CoachModel {

    private var engine: Engine? = null
    private var currentVariant: ModelVariant? = null

    override fun setVariant(variant: ModelVariant) {
        if (currentVariant != variant) {
            unload()
            currentVariant = variant
        }
    }

    override suspend fun isReady(): Boolean = engine != null

    override suspend fun load() = withContext(Dispatchers.IO) {
        if (engine != null) return@withContext

        val variant = currentVariant ?: ModelVariant.GEMMA_3N_1B
        val config = ModelConfig.configs[variant] ?: run {
            Log.e(TAG, "No ModelConfig entry for variant $variant")
            throw RuntimeException("No configuration found for model variant: $variant")
        }

        val modelFile = File(context.filesDir, "${ModelConfig.MODEL_DIR}/${config.fileName}")
        Log.d(TAG, "Model path: ${modelFile.absolutePath}")
        Log.d(TAG, "File exists: ${modelFile.exists()}, size: ${if (modelFile.exists()) "${modelFile.length() / 1_048_576} MB" else "N/A"}")

        if (!modelFile.exists()) {
            val msg = "Model file '${config.fileName}' not found at ${modelFile.absolutePath}. Download it via onboarding."
            Log.e(TAG, msg)
            throw RuntimeException(msg)
        }

        val engineConfig = EngineConfig(
            modelPath = modelFile.absolutePath,
            backend = Backend.CPU()
        )

        Log.d(TAG, "Creating Engine for ${config.fileName}...")
        try {
            val newEngine = Engine(engineConfig)
            Log.d(TAG, "Engine created. Calling initialize() — may take up to 10s...")
            newEngine.initialize()
            engine = newEngine
            Log.d(TAG, "Engine initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Engine initialization failed: ${e.message}", e)
            unload()
            throw RuntimeException("LiteRT-LM engine failed to initialize: ${e.message}", e)
        }
    }

    override fun unload() {
        engine?.close()
        engine = null
    }

    override suspend fun generate(prompt: String): Flow<String> {
        val currentEngine = engine ?: throw IllegalStateException("Model not loaded. Call load() first.")
        return flow {
            val convConfig = ConversationConfig(
                systemInstruction = Contents.of(
                    "You are a knowledgeable running and cycling coach. Be concise and practical."
                )
            )
            currentEngine.createConversation(convConfig).use { conv ->
                Log.d(TAG, "Sending prompt (${prompt.length} chars)...")
                conv.sendMessageAsync(prompt)
                    .catch { e ->
                        Log.e(TAG, "sendMessageAsync error: ${e.message}", e)
                        throw e
                    }
                    .collect { token -> emit(token.toString()) }
            }
        }.flowOn(Dispatchers.IO)
    }
}
