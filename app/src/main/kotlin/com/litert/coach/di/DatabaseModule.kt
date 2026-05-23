package com.litert.coach.di

import android.content.Context
import androidx.room.Room
import com.litert.coach.BuildConfig
import com.litert.coach.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase {
        val builder = Room.databaseBuilder(ctx, AppDatabase::class.java, "coach_db")
        if (BuildConfig.DEBUG) builder.fallbackToDestructiveMigration()
        return builder.build()
    }

    @Provides fun provideUserProfileDao(db: AppDatabase) = db.userProfileDao()
    @Provides fun providePlanDao(db: AppDatabase) = db.planDao()
    @Provides fun provideWorkoutLogDao(db: AppDatabase) = db.workoutLogDao()
    @Provides fun provideChatDao(db: AppDatabase) = db.chatDao()
}
