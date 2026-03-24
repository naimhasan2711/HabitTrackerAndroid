package com.abdur.rahman.habittracker.data.local.dao

import androidx.room.*
import com.abdur.rahman.habittracker.data.model.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    
    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingsEntity?
    
    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    fun getSettingFlow(key: String): Flow<SettingsEntity?>
    
    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingsEntity>>
    
    @Query("SELECT value FROM settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<SettingsEntity>)
    
    @Update
    suspend fun updateSetting(setting: SettingsEntity)
    
    @Delete
    suspend fun deleteSetting(setting: SettingsEntity)
    
    @Query("DELETE FROM settings WHERE `key` = :key")
    suspend fun deleteSettingByKey(key: String)
    
    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()
}
