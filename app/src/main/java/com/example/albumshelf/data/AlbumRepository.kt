package com.example.albumshelf.data

import com.example.albumshelf.data.local.AlbumDao
import com.example.albumshelf.data.local.AlbumEntity
import com.example.albumshelf.data.remote.ITunesApiService
import com.example.albumshelf.data.remote.dto.AlbumDto
import kotlinx.coroutines.flow.Flow

class AlbumRepository(
    private val apiService: ITunesApiService,
    private val albumDao: AlbumDao
) {
    fun getLibraryAlbums(): Flow<List<AlbumEntity>> = albumDao.getAllAlbums()

    fun getAlbumById(albumId: String): Flow<AlbumEntity?> = albumDao.getAlbumById(albumId)

    suspend fun getTopRatedAlbums(): List<AlbumEntity> = albumDao.getTopRatedAlbums()

    suspend fun searchRemoteAlbums(query: String): List<AlbumDto> {
        return apiService.searchAlbums(term = query).results
    }

    suspend fun findAlbumByNameAndArtist(albumName: String, artistName: String): AlbumDto? {
        val searchTerm = "$albumName $artistName"
        val response = apiService.searchAlbums(term = searchTerm, limit = 10)
        return response.results.firstOrNull {
            it.collectionName.contains(albumName, ignoreCase = true) &&
                    it.artistName.contains(artistName, ignoreCase = true)
        }
    }

    suspend fun saveAlbumToLibrary(album: AlbumEntity) {
        albumDao.insertAlbum(album)
    }

    suspend fun deleteAlbumFromLibrary(albumId: String) {
        albumDao.deleteAlbum(albumId)
    }
}