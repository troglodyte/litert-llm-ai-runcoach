package com.litert.coach.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litert.coach.domain.model.TrainingPlan
import com.litert.coach.domain.repository.PlanRepository
import com.litert.coach.domain.usecase.GeneratePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanScreenState(
    val plan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null
)

sealed class PlanIntent {
    data object GeneratePlan : PlanIntent()
    data object DismissError : PlanIntent()
}

sealed class PlanEffect {
    data object NavigateToChat : PlanEffect()
}

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val planRepo: PlanRepository,
    private val generatePlanUseCase: GeneratePlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlanScreenState(isLoading = true))
    val state: StateFlow<PlanScreenState> = _state.asStateFlow()

    private val _effects = Channel<PlanEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            planRepo.observeActivePlan().collect { plan ->
                _state.update { it.copy(plan = plan, isLoading = false) }
            }
        }
    }

    fun onIntent(intent: PlanIntent) {
        when (intent) {
            is PlanIntent.GeneratePlan -> generatePlan()
            is PlanIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    fun navigateToChat() {
        viewModelScope.launch { _effects.send(PlanEffect.NavigateToChat) }
    }

    private fun generatePlan() {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, error = null) }
            runCatching { generatePlanUseCase() }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Generation failed") } }
            _state.update { it.copy(isGenerating = false) }
        }
    }
}
