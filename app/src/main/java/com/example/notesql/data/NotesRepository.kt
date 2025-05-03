package com.example.notesql.data

import com.example.notesql.data.interfaces.NoteDao
import com.example.notesql.data.types.Note
import kotlinx.coroutines.flow.Flow

// The sample really likes using interfaces.
// I just so happen to agree with the process, after looking it over.
// Doing this allows for easier addition of network functions later if needed.
// I disagree with splitting the classes across multiple files though.
// Easier to keep all the related classes in one place.
interface NotesRepository {

    fun getAllNotesStream(): Flow<List<Note>>
    fun getNoteStream(id: Int): Flow<Note?>

    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun updateNote(note: Note)
}


class LocalNotesRepository(private val noteDao: NoteDao) : NotesRepository {

    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.queryAll()
    override fun getNoteStream(id: Int): Flow<Note?> = noteDao.query(id)

    override suspend fun insertNote(note: Note) = noteDao.insert(note)
    override suspend fun deleteNote(note: Note) = noteDao.delete(note)
    override suspend fun updateNote(note: Note) = noteDao.update(note)
}

