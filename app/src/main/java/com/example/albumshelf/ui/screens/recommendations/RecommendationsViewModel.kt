package com.example.albumshelf.ui.screens.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.albumshelf.data.AlbumRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class GeminiRecommendation(
    val albumName: String,
    val artistName: String,
    val reason: String
)

data class VerifiedRecommendation(
    val albumName: String,
    val artistName: String,
    val reason: String,
    val artworkUrl: String
)

data class RecommendationsUiState(
    val isLoading: Boolean = false,
    val recommendations: List<VerifiedRecommendation> = emptyList(),
    val error: String? = null
)

class RecommendationsViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    fun generateRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, recommendations = emptyList()) }
            try {
                val topAlbums = repository.getTopRatedAlbums()
                if (topAlbums.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "Añade y califica al menos un álbum para obtener recomendaciones.") }
                    return@launch
                }

                val prompt = createPrompt(topAlbums)
                val geminiJson = callGeminiApi(prompt)
                val geminiRecs = parseJson(geminiJson)

                val verifiedRecs = mutableListOf<VerifiedRecommendation>()
                for (rec in geminiRecs) {
                    val verifiedAlbum = repository.findAlbumByNameAndArtist(rec.albumName, rec.artistName)
                    if (verifiedAlbum != null) {
                        verifiedRecs.add(
                            VerifiedRecommendation(
                                albumName = verifiedAlbum.collectionName,
                                artistName = verifiedAlbum.artistName,
                                reason = rec.reason,
                                artworkUrl = verifiedAlbum.artworkUrl100.replace("100x100", "600x600")
                            )
                        )
                    }
                }

                if (verifiedRecs.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudieron verificar las recomendaciones. Inténtalo de nuevo.") }
                } else {
                    _uiState.update { it.copy(isLoading = false, recommendations = verifiedRecs) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al generar recomendaciones: ${e.message}") }
            }
        }
    }

    private fun createPrompt(albums: List<com.example.albumshelf.data.local.AlbumEntity>): String {
        val albumList = albums.joinToString(", ") { "'${it.name}' de ${it.artist}" }
        return "Actúa como un historiador musical y experto en recomendaciones. Tu objetivo principal es la precisión de los datos. " +
                "Basado en que mis álbumes favoritos son: $albumList. " +
                "Recomiéndame 5 álbumes similares. " +
                "INSTRUCCIONES CRÍTICAS: " +
                "1. DEBES verificar que el álbum recomendado realmente pertenezca al artista recomendado. No inventes álbumes ni los atribuyas incorrectamente. La precisión es más importante que la creatividad. " +
                "2. La razón debe ser una explicación concisa de la similitud musical (género, sonido, influencia, época). " +
                "3. Tu respuesta completa DEBE SER ÚNICAMENTE un array JSON válido que coincida con este formato exacto: " +
                "[{\"albumName\": \"string\", \"artistName\": \"string\", \"reason\": \"string\"}]. No incluyas ningún texto antes o después del array JSON."
    }

    private suspend fun callGeminiApi(prompt: String): String {
        return withContext(Dispatchers.IO) {
            val apiKey = "AIzaSyAHN5Q34inCcDk2ao_22qg6vRWJjJ8anIs"
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=$apiKey")

            val textPart = JsonObject().apply { addProperty("text", prompt) }
            val partsArray = JsonArray().apply { add(textPart) }
            val content = JsonObject().apply { add("parts", partsArray) }
            val contentsArray = JsonArray().apply { add(content) }
            val requestBody = JsonObject().apply { add("contents", contentsArray) }
            val body = Gson().toJson(requestBody)

            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                connection.outputStream.use { it.write(body.toByteArray()) }

                if (connection.responseCode in 200..299) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = Gson().fromJson(response, JsonObject::class.java)

                    val candidates = jsonResponse.getAsJsonArray("candidates")
                    val firstCandidate = candidates?.get(0)?.asJsonObject
                    val content = firstCandidate?.get("content")?.asJsonObject
                    val parts = content?.getAsJsonArray("parts")
                    val firstPart = parts?.get(0)?.asJsonObject
                    var text = firstPart?.get("text")?.asString

                    if (text != null) {
                        val startIndex = text.indexOf('[')
                        val endIndex = text.lastIndexOf(']')
                        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                            text = text.substring(startIndex, endIndex + 1)
                        }
                    }

                    return@withContext text ?: throw Exception("Respuesta JSON inesperada de la API.")
                } else {
                    val errorStream = connection.errorStream ?: connection.inputStream
                    val errorResponse = InputStreamReader(errorStream).use { it.readText() }
                    throw Exception("Error de la API (${connection.responseCode}): $errorResponse")
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parseJson(jsonString: String): List<GeminiRecommendation> {
        val type = object : TypeToken<List<GeminiRecommendation>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}


class RecommendationsViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}