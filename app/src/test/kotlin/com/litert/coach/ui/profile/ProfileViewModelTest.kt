package com.litert.coach.ui.profile

import com.litert.coach.ai.ModelDownloader
import com.litert.coach.ai.ModelVariant
import com.litert.coach.domain.model.UserProfile
import com.litert.coach.domain.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun fakeProfile() = UserProfile(
        name = "Test", age = 30, weightKg = 70f, heightCm = 175f,
        activityTypes = listOf("Running"), fitnessLevel = "Beginner",
        weeklyAvailabilityDays = 3, currentWeeklyDistanceKm = 10f,
        trainingStyle = "Balanced",
        modelVariant = ModelVariant.GEMMA_3N_1B.name
    )

    @Test
    fun `initial state reflects downloaded variants from ModelDownloader`() {
        val downloader = mockk<ModelDownloader>()
        every { downloader.isDownloaded(ModelVariant.GEMMA_3N_1B) } returns true
        every { downloader.isDownloaded(ModelVariant.GEMMA_3N_4B) } returns false
        val profileRepo = mockk<ProfileRepository>(relaxed = true)
        every { profileRepo.observe() } returns flowOf(fakeProfile())

        val vm = ProfileViewModel(profileRepo, mockk(relaxed = true), downloader)

        assertTrue(ModelVariant.GEMMA_3N_1B in vm.state.value.downloadedVariants)
        assertFalse(ModelVariant.GEMMA_3N_4B in vm.state.value.downloadedVariants)
    }

    @Test
    fun `refreshDownloadStatus re-evaluates downloaded variants`() {
        val downloader = mockk<ModelDownloader>()
        every { downloader.isDownloaded(any()) } returns false
        val profileRepo = mockk<ProfileRepository>(relaxed = true)
        every { profileRepo.observe() } returns flowOf(fakeProfile())

        val vm = ProfileViewModel(profileRepo, mockk(relaxed = true), downloader)
        assertTrue(vm.state.value.downloadedVariants.isEmpty())

        every { downloader.isDownloaded(ModelVariant.GEMMA_3N_1B) } returns true
        vm.refreshDownloadStatus()

        assertTrue(ModelVariant.GEMMA_3N_1B in vm.state.value.downloadedVariants)
        assertFalse(ModelVariant.GEMMA_3N_4B in vm.state.value.downloadedVariants)
    }
}
