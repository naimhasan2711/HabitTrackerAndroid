package com.nakibul.hassan.habittracker.di

import android.content.Context
import com.nakibul.hassan.habittracker.notification.NotificationHelper
import com.nakibul.hassan.habittracker.notification.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context
    ): ReminderScheduler {
        return ReminderScheduler(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
        reminderScheduler: ReminderScheduler
    ): NotificationHelper {
        return NotificationHelper(context, reminderScheduler)
    }
}

