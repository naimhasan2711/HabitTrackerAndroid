package com.abdur.rahman.habittracker.data.repository

import com.abdur.rahman.habittracker.data.local.dao.SettingsDao
import com.abdur.rahman.habittracker.data.model.SettingsEntity
import com.abdur.rahman.habittracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    
    override suspend fun getSetting(key: String): String? {
        return settingsDao.getSettingValue(key)
    }
    
    override fun getSettingFlow(key: String): Flow<String?> {
        return settingsDao.getSettingFlow(key).map { it?.value }
    }
    
    override suspend fun setSetting(key: String, value: String) {
        settingsDao.insertSetting(SettingsEntity(key, value))
    }
    
    override suspend fun deleteSetting(key: String) {
        settingsDao.deleteSettingByKey(key)
    }
    
    override fun getAllSettings(): Flow<Map<String, String>> {
        return settingsDao.getAllSettings().map { settings ->
            settings.associate { it.key to it.value }
        }
    }
}
