package com.litert.coach.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litert.coach.ai.ModelConfig
import com.litert.coach.ai.ModelVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: ProfileViewModel = hiltViewModel(),
    onNavigateToDownload: (ModelVariant) -> Unit = {}
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        vm.refreshDownloadStatus()
        onPauseOrDispose { }
    }

    if (state.isLoading) {
        Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                CircularProgressIndicator()
            }
        }
        return
    }

    val profile = state.profile ?: return
    var name by rememberSaveable { mutableStateOf(profile.name) }
    var age by rememberSaveable { mutableStateOf(profile.age.toString()) }
    var weightKg by rememberSaveable { mutableStateOf(profile.weightKg.toString()) }
    var heightCm by rememberSaveable { mutableStateOf(profile.heightCm.toString()) }
    var injuriesNotes by rememberSaveable { mutableStateOf(profile.injuriesNotes ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            SectionHeader("Personal Info")
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = weightKg,
                onValueChange = { weightKg = it },
                label = { Text("Weight (${if (profile.preferredUnits == "metric") "kg" else "lb"})") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = heightCm,
                onValueChange = { heightCm = it },
                label = { Text("Height (${if (profile.preferredUnits == "metric") "cm" else "in"})") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = injuriesNotes,
                onValueChange = { injuriesNotes = it },
                label = { Text("Injuries / Limitations (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("AI Model")
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Select which on-device model to use. A larger model gives better responses but uses more storage.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            val currentVariant = ModelVariant.entries.firstOrNull { it.name == profile.modelVariant }
                ?: ModelVariant.GEMMA_3N_1B

            ModelVariant.entries.forEach { variant ->
                val isDownloaded = variant in state.downloadedVariants
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentVariant == variant,
                        onClick = { vm.switchModel(variant) }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(variant.displayName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "~${variant.fileSizeMb} MB  •  ${ModelConfig.configs[variant]?.fileName ?: ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isDownloaded) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Downloaded",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        OutlinedButton(
                            onClick = { onNavigateToDownload(variant) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Download", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            if (state.savedFeedback) {
                Text(
                    "Saved!",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    vm.saveProfile(
                        profile.copy(
                            name = name.trim(),
                            age = age.toIntOrNull() ?: profile.age,
                            weightKg = weightKg.toFloatOrNull() ?: profile.weightKg,
                            heightCm = heightCm.toFloatOrNull() ?: profile.heightCm,
                            injuriesNotes = injuriesNotes.trim().ifBlank { null }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                Text(if (state.isSaving) "Saving…" else "Save Changes")
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary)
}
