package com.litert.coach.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litert.coach.domain.model.WorkoutLog
import com.litert.coach.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryScreenState(
    val workouts: List<WorkoutLog> = emptyList(),
    val isLoading: Boolean = true,
    val selectedWorkout: WorkoutLog? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryScreenState())
    val state: StateFlow<HistoryScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepo.observeAll().collect { logs ->
                _state.update { it.copy(workouts = logs, isLoading = false) }
            }
        }
    }

    fun selectWorkout(workout: WorkoutLog) = _state.update { it.copy(selectedWorkout = workout) }
    fun clearSelection() = _state.update { it.copy(selectedWorkout = null) }
}
