package com.example.albumshelf

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.albumshelf.data.AlbumRepository
import com.example.albumshelf.data.local.AppDatabase
import com.example.albumshelf.data.remote.RetrofitInstance

class AlbumShelfApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(private val context: Context) {
    private val db by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "album-shelf-db"
        ).build()
    }


    val albumRepository by lazy {
        AlbumRepository(RetrofitInstance.api, db.albumDao())
    }
}
