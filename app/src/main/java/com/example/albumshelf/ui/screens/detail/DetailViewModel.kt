package com.example.albumshelf.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.albumshelf.data.AlbumRepository
import com.example.albumshelf.data.local.AlbumEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    val uiState: StateFlow<AlbumEntity?> = repository.getAlbumById(albumId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun deleteAlbum() {
        viewModelScope.launch {
            repository.deleteAlbumFromLibrary(albumId)
        }
    }

    companion object {
        fun Factory(repository: AlbumRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return DetailViewModel(repository, extras.createSavedStateHandle()) as T
            }
        }
    }
}
