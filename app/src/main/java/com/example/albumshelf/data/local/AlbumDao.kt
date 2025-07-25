package com.example.albumshelf.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM library_albums ORDER BY name ASC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM library_albums WHERE id = :albumId")
    fun getAlbumById(albumId: String): Flow<AlbumEntity?>


    @Query("SELECT * FROM library_albums ORDER BY rating DESC LIMIT 5")
    suspend fun getTopRatedAlbums(): List<AlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Query("DELETE FROM library_albums WHERE id = :albumId")
    suspend fun deleteAlbum(albumId: String)
}