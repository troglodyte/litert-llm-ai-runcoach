package com.litert.coach.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import com.litert.coach.ai.CoachModel
import com.litert.coach.ai.Gemma3nModel
import com.litert.coach.data.repository.ChatRepositoryImpl
import com.litert.coach.data.repository.PlanRepositoryImpl
import com.litert.coach.data.repository.ProfileRepositoryImpl
import com.litert.coach.data.repository.WorkoutRepositoryImpl
import com.litert.coach.domain.repository.ChatRepository
import com.litert.coach.domain.repository.PlanRepository
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds @Singleton abstract fun bindCoachModel(impl: Gemma3nModel): CoachModel
    @Binds @Singleton abstract fun bindProfileRepo(impl: ProfileRepositoryImpl): ProfileRepository
    @Binds @Singleton abstract fun bindPlanRepo(impl: PlanRepositoryImpl): PlanRepository
    @Binds @Singleton abstract fun bindWorkoutRepo(impl: WorkoutRepositoryImpl): WorkoutRepository
    @Binds @Singleton abstract fun bindChatRepo(impl: ChatRepositoryImpl): ChatRepository

    companion object {
        @Provides @Singleton
        fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { File(ctx.filesDir, "datastore/coach_prefs.preferences_pb") }
            )

        @Provides @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .build()
    }
}
