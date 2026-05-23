package com.litert.coach.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litert.coach.ai.Gemma3nModel
import com.litert.coach.ai.ModelDownloader
import com.litert.coach.ai.ModelVariant
import com.litert.coach.domain.model.UserProfile
import com.litert.coach.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val savedFeedback: Boolean = false,
    val downloadedVariants: Set<ModelVariant> = emptySet()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val model: Gemma3nModel,
    private val modelDownloader: ModelDownloader
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileScreenState())
    val state: StateFlow<ProfileScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepo.observe().collect { profile ->
                _state.update { it.copy(profile = profile, isLoading = false) }
            }
        }
        refreshDownloadStatus()
    }

    fun saveProfile(updated: UserProfile) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            profileRepo.save(updated)
            _state.update { it.copy(isSaving = false, savedFeedback = true) }
            delay(2000)
            _state.update { it.copy(savedFeedback = false) }
        }
    }

    fun switchModel(variant: ModelVariant) {
        viewModelScope.launch {
            val profile = _state.value.profile ?: return@launch
            profileRepo.save(profile.copy(modelVariant = variant.name))
            model.setVariant(variant)
        }
    }

    fun refreshDownloadStatus() {
        val downloaded = ModelVariant.entries.filter { modelDownloader.isDownloaded(it) }.toSet()
        _state.update { it.copy(downloadedVariants = downloaded) }
    }
}
