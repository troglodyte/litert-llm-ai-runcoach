package com.litert.coach.domain.repository

import com.litert.coach.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observe(): Flow<UserProfile?>
    suspend fun get(): UserProfile?
    suspend fun save(profile: UserProfile)
    suspend fun setModelDownloaded(downloaded: Boolean)
}
