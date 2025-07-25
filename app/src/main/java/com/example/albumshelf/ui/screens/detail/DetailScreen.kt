package com.example.albumshelf.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.albumshelf.AlbumShelfApp
import com.example.albumshelf.navigation.Screen
import com.example.albumshelf.ui.components.StarDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    app: AlbumShelfApp
) {
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory(app.container.albumRepository))
    val album by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Álbum") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    album?.let {
                        IconButton(onClick = {
                            navController.navigate(Screen.AddOrEdit.createEditRoute(it.id))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Reseña")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        album?.let { albumDetails ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = albumDetails.artworkUrl,
                    contentDescription = albumDetails.name,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = albumDetails.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = albumDetails.artist, style = MaterialTheme.typography.titleLarge)
                Text(text = albumDetails.year, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                StarDisplay(rating = albumDetails.rating)

                Spacer(modifier = Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tu Reseña", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (albumDetails.review.isNotBlank()) albumDetails.review else "No has escrito una reseña.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.deleteAlbum()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar de la Biblioteca")
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
