package com.nakibul.hassan.habittracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSetting(key: String): String?
    fun getSettingFlow(key: String): Flow<String?>
    suspend fun setSetting(key: String, value: String)
    suspend fun deleteSetting(key: String)
    fun getAllSettings(): Flow<Map<String, String>>
}

