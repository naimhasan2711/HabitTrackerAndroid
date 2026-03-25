package com.nakibul.hassan.habittracker.di

import android.content.Context
import androidx.room.Room
import com.nakibul.hassan.habittracker.data.local.HabitDatabase
import com.nakibul.hassan.habittracker.data.local.dao.*
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
    fun provideDatabase(@ApplicationContext context: Context): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            HabitDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideCategoryDao(database: HabitDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }
    
    @Provides
    fun provideHabitLogDao(database: HabitDatabase): HabitLogDao {
        return database.habitLogDao()
    }
    
    @Provides
    fun provideStreakDao(database: HabitDatabase): StreakDao {
        return database.streakDao()
    }
    
    @Provides
    fun provideSettingsDao(database: HabitDatabase): SettingsDao {
        return database.settingsDao()
    }
}

