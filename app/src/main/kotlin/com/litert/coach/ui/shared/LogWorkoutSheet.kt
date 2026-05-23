package com.litert.coach.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.litert.coach.domain.model.WorkoutLog
import com.litert.coach.domain.usecase.LogWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

private val ACTIVITY_TYPES = listOf("Running", "Trail Running", "Cycling", "Swimming", "Walking")

data class LogWorkoutFormState(
    val activityType: String = "Running",
    val distanceKm: String = "",
    val durationHours: String = "0",
    val durationMinutes: String = "30",
    val durationSeconds: String = "0",
    val avgHeartRate: String = "",
    val perceivedEffort: Int = 5,
    val notes: String = "",
    val isSaving: Boolean = false
)

@HiltViewModel
class LogWorkoutViewModel @Inject constructor(
    private val logWorkoutUseCase: LogWorkoutUseCase
) : ViewModel() {

    private val _form = MutableStateFlow(LogWorkoutFormState())
    val form: StateFlow<LogWorkoutFormState> = _form.asStateFlow()

    fun updateActivityType(v: String) = _form.update { it.copy(activityType = v) }
    fun updateDistance(v: String) = _form.update { it.copy(distanceKm = v) }
    fun updateDurationHours(v: String) = _form.update { it.copy(durationHours = v) }
    fun updateDurationMinutes(v: String) = _form.update { it.copy(durationMinutes = v) }
    fun updateDurationSeconds(v: String) = _form.update { it.copy(durationSeconds = v) }
    fun updateAvgHeartRate(v: String) = _form.update { it.copy(avgHeartRate = v) }
    fun updatePerceivedEffort(v: Int) = _form.update { it.copy(perceivedEffort = v) }
    fun updateNotes(v: String) = _form.update { it.copy(notes = v) }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            _form.update { it.copy(isSaving = true) }
            val f = _form.value
            val totalSeconds = (f.durationHours.toIntOrNull() ?: 0) * 3600 +
                (f.durationMinutes.toIntOrNull() ?: 0) * 60 +
                (f.durationSeconds.toIntOrNull() ?: 0)
            val log = WorkoutLog(
                id = 0,
                loggedAt = System.currentTimeMillis(),
                activityType = f.activityType,
                distanceKm = f.distanceKm.toFloatOrNull(),
                durationSeconds = totalSeconds,
                avgHeartRate = f.avgHeartRate.toIntOrNull(),
                perceivedEffort = f.perceivedEffort,
                notes = f.notes.trim().ifBlank { null }
            )
            runCatching { logWorkoutUseCase(log) }
            _form.update { it.copy(isSaving = false) }
            onDone()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWorkoutSheet(
    onDismiss: () -> Unit,
    vm: LogWorkoutViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .imePadding()
        ) {
            Text("Log Workout", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))

            Text("Activity Type", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                ACTIVITY_TYPES.forEach { type ->
                    FilterChip(
                        selected = form.activityType == type,
                        onClick = { vm.updateActivityType(type) },
                        label = { Text(type) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.distanceKm,
                onValueChange = vm::updateDistance,
                label = { Text("Distance (km)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Duration", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.durationHours,
                    onValueChange = vm::updateDurationHours,
                    label = { Text("h") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = form.durationMinutes,
                    onValueChange = vm::updateDurationMinutes,
                    label = { Text("min") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = form.durationSeconds,
                    onValueChange = vm::updateDurationSeconds,
                    label = { Text("sec") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = form.avgHeartRate,
                onValueChange = vm::updateAvgHeartRate,
                label = { Text("Avg Heart Rate (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Perceived Effort: ${form.perceivedEffort}/10",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = form.perceivedEffort.toFloat(),
                onValueChange = { vm.updatePerceivedEffort(it.roundToInt()) },
                valueRange = 1f..10f,
                steps = 8
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = form.notes,
                onValueChange = vm::updateNotes,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { vm.save(onDismiss) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !form.isSaving
            ) {
                Text(if (form.isSaving) "Saving…" else "Save Workout")
            }
        }
    }
}
