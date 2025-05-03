package com.example.notesql.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


// data store access class
class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val DARK_MODE_OVERRIDE = booleanPreferencesKey("dark_mode_override")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")

        const val TAG = "PreferencesRepo"
    }

    // Null when override is unset
    // Otherwise gives the overridden dark mode state
    val darkModeOverride: Flow<Boolean?> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences", it)
            emit(emptyPreferences())
        } else throw it
    }
    .map { preferences ->
        if (preferences[DARK_MODE_OVERRIDE] == true) {
            preferences[DARK_MODE_ENABLED] ?: false
        } else null
    }

    // Enables override of dark mode setting to given state.
    suspend fun saveDarkModePreference(darkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_OVERRIDE] = true
            preferences[DARK_MODE_ENABLED] = darkModeEnabled
        }
    }

    // Returns the dark mode setting to system settings.
    suspend fun resetDarkModeOverride() {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_OVERRIDE] = false
        }
    }
}