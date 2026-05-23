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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileSetupScreen(vm: OnboardingViewModel, onNext: () -> Unit) {
    val form by vm.form.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("About You", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = form.name,
            onValueChange = vm::updateName,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = form.age,
            onValueChange = vm::updateAge,
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Units", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            listOf("metric", "imperial").forEach { unit ->
                FilterChip(
                    selected = form.preferredUnits == unit,
                    onClick = { vm.updatePreferredUnits(unit) },
                    label = { Text(if (unit == "metric") "Metric (kg/km)" else "Imperial (lb/mi)") },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        val weightLabel = if (form.preferredUnits == "metric") "Weight (kg)" else "Weight (lb)"
        OutlinedTextField(
            value = form.weightKg,
            onValueChange = vm::updateWeightKg,
            label = { Text(weightLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(12.dp))

        val heightLabel = if (form.preferredUnits == "metric") "Height (cm)" else "Height (in)"
        OutlinedTextField(
            value = form.heightCm,
            onValueChange = vm::updateHeightCm,
            label = { Text(heightLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = form.name.isNotBlank() && form.age.isNotBlank()
        ) {
            Text("Next")
        }
    }
}
