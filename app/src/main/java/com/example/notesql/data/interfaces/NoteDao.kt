package com.example.notesql.data.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notesql.data.types.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // response can be nullable because requested note may not exist
    // the sample doesn't do this but I'm the one in control here
    @Query("SELECT * FROM notes WHERE id = :id")
    fun query(id: Int): Flow<Note?>

    // not required by lab specs, but the sample has this and honestly...
    // the ability to query all notes that exist sounds very practical to have :)
    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun queryAll(): Flow<List<Note>>
}