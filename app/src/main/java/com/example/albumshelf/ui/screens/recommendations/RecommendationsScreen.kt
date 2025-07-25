package com.example.albumshelf.ui.screens.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.albumshelf.AlbumShelfApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    app: AlbumShelfApp
) {
    val viewModel: RecommendationsViewModel = viewModel(factory = RecommendationsViewModelFactory(app.container.albumRepository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones para ti") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.generateRecommendations() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Recomendaciones")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    uiState.recommendations.isNotEmpty() -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(uiState.recommendations) { recommendation ->
                                RecommendationItem(recommendation = recommendation)
                            }
                        }
                    }
                    else -> {

                        Text(
                            text = "Presiona el botón para generar recomendaciones basadas en tus álbumes mejor calificados.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationItem(recommendation: VerifiedRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = recommendation.artworkUrl,
                contentDescription = "Carátula de ${recommendation.albumName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = recommendation.albumName, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = recommendation.artistName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}