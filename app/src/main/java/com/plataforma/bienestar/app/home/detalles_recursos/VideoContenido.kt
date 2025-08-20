package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso
import kotlinx.coroutines.launch

@Composable
fun VideoContenido(
    recurso: Recurso,
    usuarioId: String,
    estaEnPrograma: Boolean,
    cambiarEstadoActividadPrograma: (nuevoEstado: String) -> Unit
) {
    val videoId = remember(recurso.enlace_contenido) {
        extractYouTubeId(recurso.enlace_contenido)
    }
    val coroutineScope = rememberCoroutineScope()

    fun establecerRecursoVisto() {
        coroutineScope.launch {
            try {
                if (!estaEnPrograma) { // No está en programa, se setea a Visto
                    ApiClient.apiService.setRecursoVisto(usuarioId, recurso.id)
                } else { // Está en programa. No se setea a visto para el recurso, sino completado para el programa
                    cambiarEstadoActividadPrograma("completado")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier) {
        if (!recurso.descripcion.isNullOrBlank()) {
            Text(
                text = recurso.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        VideoPlayer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            videoId = videoId,
            onPlayerReady = { /* Opcional: guardar referencia si se necesita */ },
            onVideoStarted = { establecerRecursoVisto() }
        )
    }
}