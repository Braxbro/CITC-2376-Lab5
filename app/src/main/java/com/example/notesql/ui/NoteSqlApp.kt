@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notesql.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.notesql.data.types.Note
import com.example.notesql.ui.NoteSqlUiState.*
import kotlinx.coroutines.runBlocking


@Composable
fun NoteSqlApp(viewModel: NoteSqlViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { AppTopBar(viewModel, scrollBehavior) },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        floatingActionButton = {
            Card(
                modifier = Modifier.size(60.dp).padding(2.dp),
                colors = CardDefaults.elevatedCardColors(),
                onClick = {
                    viewModel.openNoteAdd()
                }
            ) { Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add,"Add note")
            } }
        }
    ) { paddingValues ->
        val notesList = viewModel.notesList.collectAsState().value
        LazyColumn(contentPadding = paddingValues) {
            itemsIndexed(
                notesList
            ) { i, note ->
                Card(
                    Modifier.combinedClickable(onDoubleClick = {
                        viewModel.openNoteEdit(note)
                    }) {
                       // do nothing to prevent accidental edits when scrolling
                    }.padding(8.dp).fillMaxWidth()
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(note.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(4.dp))
                        Text(note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 4.dp).padding(bottom = 4.dp))
                    }
                }
            }
        }
        if (viewModel.uiState != NoteList) {
            val uiState = viewModel.uiState
            // you can tell when I ran out of energy to care
            Dialog(
                onDismissRequest = {
                    viewModel.closeNoteDialog()
                }
            ) {
                var title by remember { mutableStateOf("") }
                var content by remember { mutableStateOf("") }
                // wanna see me commit a programming crime
                // lua would not let me do this nonsense
                // why is kotlin like this
                val id = if (uiState is NoteEdit) {
                    title = uiState.note.title
                    content = uiState.note.content
                    uiState.note.id
                } else 0 // auto-generate id

                Card() {
                    Column(horizontalAlignment =
                        Alignment.CenterHorizontally
                    )
                    {
                        TextField(value = title,
                            onValueChange = { title = it },
                            label = {
                                Text("Title")
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                        TextField(value = content,
                            onValueChange = { content = it },
                            label = {
                                Text("Content")
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                        Box(contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.fillMaxWidth()){
                            Row() {
                                TextButton(
                                    onClick = { viewModel.closeNoteDialog() }
                                ) {
                                    Text("Dismiss")
                                }
                                if (uiState is NoteEdit) {
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                viewModel.notes.deleteNote( uiState.note )
                                            }
                                            viewModel.closeNoteDialog()
                                        }
                                    ) {
                                        Text("Delete")
                                    }
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                viewModel.notes.updateNote(
                                                    // create a new note with the same ID
                                                    Note(id = id, title = title, content = content)
                                                )
                                            }
                                            viewModel.closeNoteDialog()
                                        }
                                    ) {
                                        Text("Update")
                                    }
                                } else {
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                viewModel.notes.insertNote(
                                                    // create new note - this has id 0
                                                    // even if it doesn't look like it
                                                    Note(id = id, title = title, content = content)
                                                )
                                            }
                                            viewModel.closeNoteDialog()
                                        }
                                    ) {
                                        Text("Add")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
// The standard top bar for this notes app.
fun AppTopBar(
    viewModel: NoteSqlViewModel,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
        TopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            title = {
                Text("Notes")
            },
            actions = { DarkModeSwitch(viewModel) }
        )

}

@Composable
// A set of buttons to control light/dark mode overrides.
fun DarkModeSwitch(viewModel: NoteSqlViewModel) {
    // get current override state and dark mode state
    val override = viewModel.themeOverride.collectAsState().value
    val darkMode = override ?: isSystemInDarkTheme()

    // not my best UI, but I don't care. I had fun with the extra feature :)
    if (override == null) {
        Row() {
            Card(
                modifier = Modifier.size(60.dp).padding(2.dp).clickable {
                    runBlocking {
                        viewModel.overrideTheme(false)
                    }
                }
            ) { Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Light")
            } }
            Card(
                modifier = Modifier.size(60.dp).padding(2.dp).clickable {
                    runBlocking {
                        viewModel.overrideTheme(true)
                    }
                }
            ) { Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Dark")
            } }
        }
    } else {
        if (darkMode)
            Card(
                modifier = Modifier.size(60.dp).padding(2.dp).combinedClickable(
                    onLongClick = {
                        runBlocking {
                            viewModel.resetThemeOverride()
                        }
                    }
                ) {
                    runBlocking {
                        viewModel.overrideTheme(false)
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Light")
                }
            }
        else
            Card(
                modifier = Modifier.size(60.dp).padding(2.dp).combinedClickable(
                    onLongClick = {
                        runBlocking {
                            viewModel.resetThemeOverride()
                        }
                    }
                ) {
                    runBlocking {
                        viewModel.overrideTheme(true)
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Dark")
                }
            }
    }

}