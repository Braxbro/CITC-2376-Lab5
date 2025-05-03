package com.example.notesql.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesql.data.interfaces.NoteDao
import com.example.notesql.data.types.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var db: NoteDatabase? = null

        fun getDatabase(context: Context) : NoteDatabase {
            // in Lua this horrible mess is simply
            // return db or <fallback>
            // though I suppose ?: isn't that bad either
            return db ?: synchronized(this) {
                Room.databaseBuilder(context, NoteDatabase::class.java, "note_database")
                    .build().also {db = it}
            }
        }
    }
}