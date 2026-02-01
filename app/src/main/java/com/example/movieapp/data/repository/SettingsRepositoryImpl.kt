package com.example.movieapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) {
    private val highContrastKey = booleanPreferencesKey("high_contrast_enabled")

    val isHighContrastEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[highContrastKey] == true
        }

    suspend fun toggleHighContrast(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[highContrastKey] = enabled
        }
    }
}