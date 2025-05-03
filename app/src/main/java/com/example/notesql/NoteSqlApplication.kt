package com.example.notesql

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.notesql.data.AppContainer
import com.example.notesql.data.AppDataContainer
import com.example.notesql.data.PreferencesRepository

private const val DARK_MODE_PREFERENCE_NAME = "dark_mode_enabled"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DARK_MODE_PREFERENCE_NAME
)

class NoteSqlApplication: Application() {
    lateinit var preferencesRepository: PreferencesRepository
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        preferencesRepository = PreferencesRepository(dataStore)
    }
}