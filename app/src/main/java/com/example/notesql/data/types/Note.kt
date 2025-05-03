package com.example.notesql.data.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    // default to current time when unspecified
    val timestamp: Long = System.currentTimeMillis()
)
