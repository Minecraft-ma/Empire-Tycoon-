package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "empire_tycoon_datastore")

class GameDataStoreManager(private val context: Context) {
    companion object {
        val KEY_PLAYER_CASH = doublePreferencesKey("player_cash")
        val KEY_TOTAL_CASH_EARNED = doublePreferencesKey("total_cash_earned")
        val KEY_UPGRADES_JSON = stringPreferencesKey("upgrades_json")
        val KEY_LAST_SAVE_TIME = stringPreferencesKey("last_save_time")
        val KEY_FULL_SAVE_JSON = stringPreferencesKey("full_save_json")
    }

    suspend fun saveProgress(
        cash: Double,
        totalCashEarned: Double,
        upgradesJson: String,
        fullSaveJson: String
    ) {
        try {
            context.dataStore.edit { preferences ->
                preferences[KEY_PLAYER_CASH] = cash
                preferences[KEY_TOTAL_CASH_EARNED] = totalCashEarned
                preferences[KEY_UPGRADES_JSON] = upgradesJson
                preferences[KEY_FULL_SAVE_JSON] = fullSaveJson
                preferences[KEY_LAST_SAVE_TIME] = System.currentTimeMillis().toString()
            }
        } catch (_: Exception) {}
    }

    val playerCashFlow: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[KEY_PLAYER_CASH] ?: 50.0
    }

    val totalCashEarnedFlow: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOTAL_CASH_EARNED] ?: 50.0
    }

    val upgradesJsonFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_UPGRADES_JSON]
    }

    val fullSaveJsonFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_FULL_SAVE_JSON]
    }
}
