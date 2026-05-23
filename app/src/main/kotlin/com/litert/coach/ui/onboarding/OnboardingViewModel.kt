package com.litert.coach.ui.onboarding

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litert.coach.BuildConfig
import com.litert.coach.ai.CoachModel
import com.litert.coach.ai.DownloadState
import com.litert.coach.ai.ModelDownloader
import com.litert.coach.ai.ModelVariant
import com.litert.coach.domain.model.UserProfile
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.ui.navigation.ONBOARDING_COMPLETE_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OnboardingVM"

data class OnboardingFormState(
    val name: String = "Michael",
    val age: String = "51",
    val weightKg: String = "",
    val heightCm: String = "",
    val activityTypes: Set<String> = setOf("Running"),
    val fitnessLevel: String = "Beginner",
    val weeklyAvailabilityDays: Int = 3,
    val currentWeeklyDistanceKm: String = "10",
    val trainingStyle: String = "Balanced",
    val preferredUnits: String = "metric",
    val modelVariant: ModelVariant = ModelVariant.GEMMA_3N_1B,
    val targetEventName: String = "",
    val targetEventDistanceKm: String = "",
    val injuriesNotes: String = ""
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val modelDownloader: ModelDownloader,
    private val coachModel: CoachModel,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _form = MutableStateFlow(OnboardingFormState())
    val form: StateFlow<OnboardingFormState> = _form.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    fun updateName(v: String) = _form.update { it.copy(name = v) }
    fun updateAge(v: String) = _form.update { it.copy(age = v) }
    fun updateWeightKg(v: String) = _form.update { it.copy(weightKg = v) }
    fun updateHeightCm(v: String) = _form.update { it.copy(heightCm = v) }
    fun updatePreferredUnits(v: String) = _form.update { it.copy(preferredUnits = v) }
    fun toggleActivityType(type: String) = _form.update {
        val updated = if (type in it.activityTypes) it.activityTypes - type else it.activityTypes + type
        it.copy(activityTypes = updated)
    }
    fun updateFitnessLevel(v: String) = _form.update { it.copy(fitnessLevel = v) }
    fun updateWeeklyAvailability(v: Int) = _form.update { it.copy(weeklyAvailabilityDays = v) }
    fun updateCurrentWeeklyDistance(v: String) = _form.update { it.copy(currentWeeklyDistanceKm = v) }
    fun updateTrainingStyle(v: String) = _form.update { it.copy(trainingStyle = v) }
    fun updateTargetEventName(v: String) = _form.update { it.copy(targetEventName = v) }
    fun updateTargetEventDistance(v: String) = _form.update { it.copy(targetEventDistanceKm = v) }
    fun updateInjuriesNotes(v: String) = _form.update { it.copy(injuriesNotes = v) }
    fun updateModelVariant(v: ModelVariant) = _form.update { it.copy(modelVariant = v) }

    fun saveProfile() {
        viewModelScope.launch {
            val f = _form.value
            val profile = UserProfile(
                name = f.name.trim(),
                age = f.age.toIntOrNull() ?: 0,
                weightKg = f.weightKg.toFloatOrNull() ?: 0f,
                heightCm = f.heightCm.toFloatOrNull() ?: 0f,
                activityTypes = f.activityTypes.toList(),
                fitnessLevel = f.fitnessLevel,
                weeklyAvailabilityDays = f.weeklyAvailabilityDays,
                currentWeeklyDistanceKm = f.currentWeeklyDistanceKm.toFloatOrNull() ?: 0f,
                trainingStyle = f.trainingStyle,
                preferredUnits = f.preferredUnits,
                modelVariant = f.modelVariant.name,
                targetEventName = f.targetEventName.trim().ifBlank { null },
                targetEventDistanceKm = f.targetEventDistanceKm.toFloatOrNull(),
                injuriesNotes = f.injuriesNotes.trim().ifBlank { null }
            )
            profileRepo.save(profile)
        }
    }

    fun startDownload() {
        viewModelScope.launch {
            val token = BuildConfig.HF_TOKEN.ifBlank { null }
            val variant = _form.value.modelVariant
            Log.d(TAG, "startDownload: variant=$variant, tokenProvided=${token != null}")

            modelDownloader.download(variant, token).collect { state ->
                Log.d(TAG, "downloadState -> $state")
                _downloadState.value = state
                if (state is DownloadState.Complete) {
                    verifyAndComplete()
                }
            }
        }
    }

    private suspend fun verifyAndComplete() {
        _downloadState.value = DownloadState.Verifying
        Log.d(TAG, "verifyAndComplete: loading model to verify...")
        val success = try {
            coachModel.setVariant(_form.value.modelVariant)
            coachModel.load()
            val ready = coachModel.isReady()
            coachModel.unload()
            Log.d(TAG, "verifyAndComplete: isReady=$ready")
            ready
        } catch (e: Exception) {
            Log.e(TAG, "verifyAndComplete: exception during verification", e)
            false
        }

        if (success) {
            _downloadState.value = DownloadState.Complete
            profileRepo.setModelDownloaded(true)
            dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETE_KEY] = true }
        } else {
            _downloadState.value = DownloadState.Failed(
                "Verification failed — the model could not be loaded after download. " +
                "The file may be corrupted. Please retry."
            )
            profileRepo.setModelDownloaded(false)
        }
    }
}
