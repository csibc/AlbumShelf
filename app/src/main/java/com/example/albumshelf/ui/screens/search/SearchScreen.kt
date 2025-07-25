package com.example.albumshelf.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.albumshelf.AlbumShelfApp
import com.example.albumshelf.data.remote.dto.AlbumDto
import com.example.albumshelf.navigation.Screen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    app: AlbumShelfApp
) {
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(app.container.albumRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            label = { Text("Buscar álbum o artista") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.onSearch()
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.onSearch()
                keyboardController?.hide()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.searchResults) { album ->
                        val year = album.releaseDate.take(4)
                        val highResArtwork = album.artworkUrl100.replace("100x100", "600x600")
                        AlbumSearchResultItem(album = album, onAlbumClicked = {

                            navController.navigate(
                                Screen.AddOrEdit.createAddRoute(
                                    albumId = album.collectionId.toString(),
                                    name = album.collectionName,
                                    artist = album.artistName,
                                    artworkUrl = highResArtwork,
                                    thumbnailUrl = album.artworkUrl100,
                                    year = year
                                )
                            )
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumSearchResultItem(
    album: AlbumDto,
    onAlbumClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAlbumClicked)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.artworkUrl100,
                contentDescription = "Carátula de ${album.collectionName}",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = album.collectionName, style = MaterialTheme.typography.bodyLarge)
                Text(text = album.artistName, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
