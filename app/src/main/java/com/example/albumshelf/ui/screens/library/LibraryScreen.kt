package com.example.albumshelf.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.albumshelf.AlbumShelfApp
import com.example.albumshelf.data.local.AlbumEntity
import com.example.albumshelf.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    app: AlbumShelfApp
) {
    val viewModel: LibraryViewModel = viewModel(factory = LibraryViewModelFactory(app.container.albumRepository))
    val albums by viewModel.libraryUiState.collectAsState()

    Scaffold(
        topBar = {

            CenterAlignedTopAppBar(
                title = { Text("Mi Biblioteca") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Recommendations.route) }) {
                        Icon(Icons.Filled.StarBorder, contentDescription = "Recomendaciones")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Search.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Álbum")
            }
        }
    ) { innerPadding ->
        if (albums.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu biblioteca está vacía.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(albums) { album ->
                    AlbumGridItem(album = album, onAlbumClicked = {
                        navController.navigate(Screen.Detail.createRoute(album.id))
                    })
                }
            }
        }
    }
}

@Composable
fun AlbumGridItem(album: AlbumEntity, onAlbumClicked: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onAlbumClicked),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            AsyncImage(
                model = album.artworkUrl,
                contentDescription = album.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 250f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}