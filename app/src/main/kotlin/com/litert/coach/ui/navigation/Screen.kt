package com.litert.coach.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object ProfileSetup : Screen("profile_setup")
    data object Goals : Screen("goals")
    data object ModelPicker : Screen("model_picker")
    data object ModelDownload : Screen("model_download")
    data object ModelDownloadFromProfile : Screen("model_download_from_profile")
    data object Main : Screen("main")
    data object Chat : Screen("chat")

    sealed class Tab(route: String) : Screen(route) {
        data object Plan : Tab("tab_plan")
        data object History : Tab("tab_history")
        data object Profile : Tab("tab_profile")
    }
}
