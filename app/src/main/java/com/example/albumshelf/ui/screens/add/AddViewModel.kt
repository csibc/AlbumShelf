package com.example.albumshelf.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.albumshelf.data.AlbumRepository
import com.example.albumshelf.data.local.AlbumEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AddUiState(
    val albumName: String = "",
    val artistName: String = "",
    val artworkUrl: String = "",
    val thumbnailUrl: String = "",
    val year: String = "",
    val rating: Int = 0,
    val review: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = true
)

class AddViewModel(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    init {

        val albumName: String? = savedStateHandle["albumName"]
        if (albumName != null) {
            _uiState.update {
                it.copy(
                    albumName = albumName,
                    artistName = checkNotNull(savedStateHandle["artistName"]),
                    artworkUrl = java.net.URLDecoder.decode(checkNotNull(savedStateHandle["artworkUrl"]), "UTF-8"),
                    thumbnailUrl = java.net.URLDecoder.decode(checkNotNull(savedStateHandle["thumbnailUrl"]), "UTF-8"),
                    year = checkNotNull(savedStateHandle["year"]),
                    isLoading = false,
                    isEditing = false
                )
            }
        } else {
            // MODO EDITAR
            viewModelScope.launch {
                repository.getAlbumById(albumId).filterNotNull().firstOrNull()?.let { existingAlbum ->
                    _uiState.update {
                        it.copy(
                            albumName = existingAlbum.name,
                            artistName = existingAlbum.artist,
                            artworkUrl = existingAlbum.artworkUrl,
                            thumbnailUrl = existingAlbum.artworkUrl,
                            year = existingAlbum.year,
                            rating = existingAlbum.rating,
                            review = existingAlbum.review,
                            isLoading = false,
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun onRatingChange(newRating: Int) {
        _uiState.update { it.copy(rating = newRating) }
    }

    fun onReviewChange(newReview: String) {
        _uiState.update { it.copy(review = newReview) }
    }

    fun saveAlbum() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val albumEntity = AlbumEntity(
                id = albumId,
                name = currentState.albumName,
                artist = currentState.artistName,
                artworkUrl = currentState.artworkUrl,
                year = currentState.year,
                rating = currentState.rating,
                review = currentState.review
            )
            repository.saveAlbumToLibrary(albumEntity)
        }
    }

    companion object {
        fun Factory(repository: AlbumRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return AddViewModel(repository, extras.createSavedStateHandle()) as T
            }
        }
    }
}


