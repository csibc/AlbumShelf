package com.example.albumshelf.data.remote.dto

data class ITunesResponse(
    val results: List<AlbumDto>
)

data class AlbumDto(
    val collectionId: Long,
    val artistName: String,
    val collectionName: String,
    val artworkUrl100: String,
    val releaseDate: String
)
