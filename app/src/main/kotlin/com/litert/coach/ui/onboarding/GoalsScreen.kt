package com.litert.coach.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt

private val ACTIVITY_TYPES = listOf("Running", "Trail Running", "Cycling", "Swimming", "Walking")
private val FITNESS_LEVELS = listOf("Beginner", "Intermediate", "Advanced")
private val TRAINING_STYLES = listOf("Easy", "Balanced", "Aggressive")

@Composable
fun GoalsScreen(vm: OnboardingViewModel, onNext: () -> Unit) {
    val form by vm.form.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Your Goals", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Activity Types", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            ACTIVITY_TYPES.forEach { type ->
                FilterChip(
                    selected = type in form.activityTypes,
                    onClick = { vm.toggleActivityType(type) },
                    label = { Text(type) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fitness Level", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            FITNESS_LEVELS.forEach { level ->
                FilterChip(
                    selected = form.fitnessLevel == level,
                    onClick = { vm.updateFitnessLevel(level) },
                    label = { Text(level) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Weekly availability: ${form.weeklyAvailabilityDays} days",
            style = MaterialTheme.typography.labelLarge
        )
        Slider(
            value = form.weeklyAvailabilityDays.toFloat(),
            onValueChange = { vm.updateWeeklyAvailability(it.roundToInt()) },
            valueRange = 1f..7f,
            steps = 5
        )
        Spacer(modifier = Modifier.height(16.dp))

        val distanceUnit = if (form.preferredUnits == "metric") "km" else "mi"
        OutlinedTextField(
            value = form.currentWeeklyDistanceKm,
            onValueChange = vm::updateCurrentWeeklyDistance,
            label = { Text("Current weekly distance ($distanceUnit)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Training Style", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            TRAINING_STYLES.forEach { style ->
                FilterChip(
                    selected = form.trainingStyle == style,
                    onClick = { vm.updateTrainingStyle(style) },
                    label = { Text(style) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Target Event (optional)", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = form.targetEventName,
            onValueChange = vm::updateTargetEventName,
            label = { Text("Event name (e.g. 5K, Half Marathon)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = form.targetEventDistanceKm,
            onValueChange = vm::updateTargetEventDistance,
            label = { Text("Event distance ($distanceUnit)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = form.injuriesNotes,
            onValueChange = vm::updateInjuriesNotes,
            label = { Text("Injuries or limitations (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = form.activityTypes.isNotEmpty()
        ) {
            Text("Next")
        }
    }
}
