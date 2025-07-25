package com.example.albumshelf
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.albumshelf.navigation.AppNavigation
import com.example.albumshelf.ui.theme.AlbumShelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as AlbumShelfApp

        setContent {
            AlbumShelfTheme {
                AppNavigation(app = app)
            }
        }
    }
}
