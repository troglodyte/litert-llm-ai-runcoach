package com.litert.coach.data.db.dao

import androidx.room.*
import com.litert.coach.data.db.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observe(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun get(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET model_downloaded = :downloaded, updated_at = :now WHERE id = 1")
    suspend fun updateModelDownloaded(downloaded: Boolean, now: Long = System.currentTimeMillis())
}
