package com.litert.coach.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litert.coach.ai.CoachModel
import com.litert.coach.ai.ModelVariant
import com.litert.coach.domain.model.ChatMessage
import com.litert.coach.domain.repository.ChatRepository
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.domain.usecase.BuildPromptContextUseCase
import com.litert.coach.domain.usecase.SummarizeChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatScreenState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val modelNotReady: Boolean = false,
    val modelError: String? = null
)

sealed class ChatIntent {
    data class UpdateInput(val text: String) : ChatIntent()
    data object Send : ChatIntent()
}

sealed class ChatEffect {
    data object ScrollToBottom : ChatEffect()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
    private val profileRepo: ProfileRepository,
    private val model: CoachModel,
    private val buildPromptContext: BuildPromptContextUseCase,
    private val summarizeChat: SummarizeChatUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatScreenState())
    val state: StateFlow<ChatScreenState> = _state.asStateFlow()

    private val _effects = Channel<ChatEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            chatRepo.observeAll().collect { msgs ->
                _state.update { it.copy(messages = msgs) }
                _effects.send(ChatEffect.ScrollToBottom)
            }
        }
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.UpdateInput -> _state.update { it.copy(inputText = intent.text) }
            is ChatIntent.Send -> sendMessage()
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank() || _state.value.isStreaming) return

        viewModelScope.launch {
            _state.update { it.copy(inputText = "", isStreaming = true, streamingContent = "") }

            val userMessage = ChatMessage(
                id = 0, role = "user", content = text,
                createdAt = System.currentTimeMillis()
            )
            chatRepo.insert(userMessage)

            if (!model.isReady()) {
                val loadResult = runCatching {
                    profileRepo.get()?.let { profile ->
                        model.setVariant(ModelVariant.valueOf(profile.modelVariant))
                        model.load()
                    }
                }
                if (!model.isReady()) {
                    val reason = loadResult.exceptionOrNull()?.message
                        ?: "Model file not found. Re-run onboarding to download the model."
                    _state.update { it.copy(isStreaming = false, modelNotReady = true, modelError = reason) }
                    return@launch
                }
            }

            val prompt = buildPromptContext() + "\n\nUser: $text\nAssistant:"
            val responseBuilder = StringBuilder()

            runCatching {
                model.generate(prompt).collect { token ->
                    responseBuilder.append(token)
                    _state.update { it.copy(streamingContent = responseBuilder.toString()) }
                }
            }.onFailure { e ->
                responseBuilder.append("[Error: ${e.message}]")
            }

            val assistantMessage = ChatMessage(
                id = 0, role = "assistant", content = responseBuilder.toString(),
                createdAt = System.currentTimeMillis()
            )
            chatRepo.insert(assistantMessage)
            _state.update { it.copy(isStreaming = false, streamingContent = "") }
            _effects.send(ChatEffect.ScrollToBottom)

            runCatching { summarizeChat() }
        }
    }
}
