package com.litert.coach.data.repository

import com.litert.coach.data.db.dao.UserProfileDao
import com.litert.coach.domain.model.UserProfile
import com.litert.coach.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : ProfileRepository {
    override fun observe(): Flow<UserProfile?> = dao.observe().map { it?.toDomain() }
    override suspend fun get(): UserProfile? = dao.get()?.toDomain()
    override suspend fun save(profile: UserProfile) = dao.upsert(profile.toEntity())
    override suspend fun setModelDownloaded(downloaded: Boolean) =
        dao.updateModelDownloaded(downloaded)
}
