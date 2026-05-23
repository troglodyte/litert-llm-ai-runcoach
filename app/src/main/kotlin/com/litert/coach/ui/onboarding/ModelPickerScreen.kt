package com.litert.coach.ui.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litert.coach.ai.ModelVariant

@Composable
fun ModelPickerScreen(vm: OnboardingViewModel, onNext: () -> Unit) {
    val form by vm.form.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Choose Your Model", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "The AI model runs entirely on your device. Pick based on your storage and speed preference.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        ModelVariant.entries.forEach { variant ->
            val config = com.litert.coach.ai.ModelConfig.configs[variant]
            val selected = form.modelVariant == variant
            val borderColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(2.dp, borderColor, MaterialTheme.shapes.medium)
                    .clickable { vm.updateModelVariant(variant) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected, onClick = { vm.updateModelVariant(variant) })
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(variant.displayName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "~${variant.fileSizeMb} MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (config != null) {
                        Text(
                            config.fileName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                vm.saveProfile()
                onNext()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download Model")
        }
    }
}
