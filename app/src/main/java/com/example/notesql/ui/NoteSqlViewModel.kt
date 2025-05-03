package com.example.notesql.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notesql.NoteSqlApplication
import com.example.notesql.data.NotesRepository
import com.example.notesql.data.PreferencesRepository
import com.example.notesql.data.types.Note
import com.example.notesql.ui.NoteSqlUiState.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

// gotta love cursed imports
sealed interface NoteSqlUiState {
    object NoteList : NoteSqlUiState
    object NoteAdd : NoteSqlUiState
    data class NoteEdit(val note: Note) : NoteSqlUiState
}

class NoteSqlViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    // this is hideous
    val themeOverride: StateFlow<Boolean?> =
        preferencesRepository.darkModeOverride.map { darkModeOverride ->
            darkModeOverride
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking {
                preferencesRepository.darkModeOverride.first()
            }
        )
    val notesList: StateFlow<List<Note>> =
        notesRepository.getAllNotesStream().map {
            notesStream -> notesStream
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking {
                notesRepository.getAllNotesStream().first()
            }
        )
    // THE CURSED IMPORT RETURNS
    var uiState: NoteSqlUiState by mutableStateOf(NoteList)
        private set

    val notes: NotesRepository = notesRepository

    // State control functions
    fun openNoteEdit(note: Note) {
        uiState = NoteEdit(note)
    }
    fun openNoteAdd() {
        uiState = NoteAdd
    }
    fun closeNoteDialog() {
        uiState = NoteList
    }

    fun overrideTheme(override: Boolean) {
        // If it's not runBlocking then it won't update the theme right
        runBlocking {
            preferencesRepository.saveDarkModePreference(override)
        }
    }
    fun resetThemeOverride() {
        runBlocking {
            preferencesRepository.resetDarkModeOverride()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NoteSqlApplication)
                NoteSqlViewModel(
                    application.preferencesRepository,
                    application.container.notesRepository
                )
            }
        }
    }
}