package com.example.albumshelf.data.remote

import com.example.albumshelf.data.remote.dto.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun searchAlbums(
        @Query("term") term: String,
        @Query("entity") entity: String = "album",
        @Query("limit") limit: Int = 50
    ): ITunesResponse
}