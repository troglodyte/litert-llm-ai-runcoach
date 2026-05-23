package com.litert.coach.ui.plan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.litert.coach.domain.model.PlannedWorkout

private val DAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun WorkoutCard(workout: PlannedWorkout, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = DAY_NAMES.getOrElse(workout.dayOfWeek - 1) { "?" },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${workout.activityType} — ${workout.workoutType}",
                    style = MaterialTheme.typography.titleSmall
                )
                if (workout.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workout.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    workout.targetDistanceKm?.let {
                        Chip("${"%.1f".format(it)} km")
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    workout.targetDurationMin?.let {
                        Chip("${it} min")
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    workout.intensityZone?.let { Chip("Zone $it") }
                }
            }
        }
    }
}

@Composable
private fun Chip(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .then(
                Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            ),
    )
}
