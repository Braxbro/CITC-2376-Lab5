package com.example.notesql.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

data class NoteSqlThemeState(
    // Null when no override is active
    val darkModeOverride: Boolean?
)

class NoteSqlViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    // this is hideous
    val themeState: StateFlow<NoteSqlThemeState> =
        preferencesRepository.darkModeOverride.map { darkModeOverride ->
            NoteSqlThemeState(darkModeOverride)
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking {
                NoteSqlThemeState(
                    preferencesRepository.darkModeOverride.first()
                )
            }
        )
    // THE CURSED IMPORT RETURNS
    val uiState: NoteSqlUiState by mutableStateOf(NoteList)

    val settings: PreferencesRepository = preferencesRepository
    val notes: NotesRepository = notesRepository

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