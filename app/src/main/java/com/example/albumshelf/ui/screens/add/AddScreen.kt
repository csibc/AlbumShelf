package com.example.albumshelf.ui.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.albumshelf.AlbumShelfApp
import com.example.albumshelf.navigation.Screen
import com.example.albumshelf.ui.components.StarRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavController,
    app: AlbumShelfApp
) {
    val viewModel: AddViewModel = viewModel(factory = AddViewModel.Factory(app.container.albumRepository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar Reseña" else "Añadir a Biblioteca") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.artworkUrl)
                    .placeholderMemoryCacheKey(uiState.thumbnailUrl)
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model = imageRequest,
                    contentDescription = uiState.albumName,
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = uiState.albumName, style = MaterialTheme.typography.headlineSmall)
                Text(text = uiState.artistName, style = MaterialTheme.typography.titleMedium)
                Text(text = uiState.year, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(24.dp))

                Text("Tu Calificación", style = MaterialTheme.typography.titleMedium)
                StarRating(
                    rating = uiState.rating,
                    onRatingChange = { viewModel.onRatingChange(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.review,
                    onValueChange = { viewModel.onReviewChange(it) },
                    label = { Text("Tu reseña") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.saveAlbum()
                        navController.navigate(Screen.Library.route) {
                            popUpTo(Screen.Library.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.rating > 0
                ) {
                    Text(if (uiState.isEditing) "Guardar Cambios" else "Guardar en Biblioteca")
                }
            }
        }
    }
}


