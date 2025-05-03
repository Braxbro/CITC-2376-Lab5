package com.example.notesql.data

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme

interface AppContainer {
    val notesRepository: NotesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        LocalNotesRepository(NoteDatabase.getDatabase(context).noteDao())
    }
}