package com.nakibul.hassan.habittracker.di

import com.nakibul.hassan.habittracker.data.repository.*
import com.nakibul.hassan.habittracker.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        habitRepositoryImpl: HabitRepositoryImpl
    ): HabitRepository
    
    @Binds
    @Singleton
    abstract fun bindHabitLogRepository(
        habitLogRepositoryImpl: HabitLogRepositoryImpl
    ): HabitLogRepository
    
    @Binds
    @Singleton
    abstract fun bindStreakRepository(
        streakRepositoryImpl: StreakRepositoryImpl
    ): StreakRepository
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}

