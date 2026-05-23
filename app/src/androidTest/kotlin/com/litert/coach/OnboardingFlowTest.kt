package com.litert.coach

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litert.coach.ui.onboarding.WelcomeScreen
import com.litert.coach.ui.theme.CoachTheme
import org.junit.Rule
import org.junit.Test

/**
 * Smoke test verifying the onboarding entry point renders correctly and
 * the "Get Started" action triggers navigation.
 */
class OnboardingFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeScreen_displaysTitle() {
        composeTestRule.setContent {
            CoachTheme { WelcomeScreen(onGetStarted = {}) }
        }
        composeTestRule.onNodeWithText("LiteRT Running Coach").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_getStartedButton_isDisplayed() {
        composeTestRule.setContent {
            CoachTheme { WelcomeScreen(onGetStarted = {}) }
        }
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_getStartedClick_invokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            CoachTheme { WelcomeScreen(onGetStarted = { clicked = true }) }
        }
        composeTestRule.onNodeWithText("Get Started").performClick()
        assert(clicked) { "onGetStarted callback was not invoked" }
    }
}
