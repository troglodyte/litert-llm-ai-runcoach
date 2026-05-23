package com.litert.coach.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.litert.coach.ui.onboarding.DownloadScreen
import com.litert.coach.ui.onboarding.GoalsScreen
import com.litert.coach.ui.onboarding.ModelPickerScreen
import com.litert.coach.ui.onboarding.OnboardingViewModel
import com.litert.coach.ui.onboarding.ProfileSetupScreen
import com.litert.coach.ui.onboarding.WelcomeScreen
import kotlinx.coroutines.flow.firstOrNull

val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")

@Composable
fun NavGraph(dataStore: DataStore<Preferences>) {
    var onboardingComplete by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        onboardingComplete = dataStore.data.firstOrNull()?.get(ONBOARDING_COMPLETE_KEY) ?: false
    }

    val startDestination = when (onboardingComplete) {
        null -> Screen.Splash.route
        true -> Screen.Main.route
        false -> Screen.Welcome.route
    }

    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            // Blank while DataStore resolves; LaunchedEffect above will re-trigger NavHost
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.ProfileSetup.route) })
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(vm = onboardingViewModel, onNext = { navController.navigate(Screen.Goals.route) })
        }

        composable(Screen.Goals.route) {
            GoalsScreen(vm = onboardingViewModel, onNext = { navController.navigate(Screen.ModelPicker.route) })
        }

        composable(Screen.ModelPicker.route) {
            ModelPickerScreen(
                vm = onboardingViewModel,
                onNext = { navController.navigate(Screen.ModelDownload.route) }
            )
        }

        composable(Screen.ModelDownload.route) {
            DownloadScreen(
                vm = onboardingViewModel,
                onBack = { navController.popBackStack() },
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ModelDownloadFromProfile.route) {
            DownloadScreen(
                vm = onboardingViewModel,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            MainScaffold(
                onNavigateToDownload = { variant ->
                    onboardingViewModel.updateModelVariant(variant)
                    navController.navigate(Screen.ModelDownloadFromProfile.route)
                }
            )
        }
    }
}
