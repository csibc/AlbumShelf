package com.example.albumshelf.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val artworkUrl: String,
    val year: String,
    val rating: Int,
    val review: String
)

