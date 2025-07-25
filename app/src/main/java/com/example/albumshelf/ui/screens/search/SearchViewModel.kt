package com.example.albumshelf.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.albumshelf.data.AlbumRepository
import com.example.albumshelf.data.remote.dto.AlbumDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SearchUiState(
    val query: String = "",
    val searchResults: List<AlbumDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


class SearchViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun onSearch() {
        if (_uiState.value.query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = repository.searchRemoteAlbums(_uiState.value.query)
                _uiState.update {
                    it.copy(isLoading = false, searchResults = results)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al buscar: ${e.message}")
                }
            }
        }
    }
}


class SearchViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
