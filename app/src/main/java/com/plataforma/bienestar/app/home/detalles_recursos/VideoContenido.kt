package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.plataforma.bienestar.data.api.model.Recurso

@Composable
fun VideoContenido(
    recurso: Recurso,
    modifier: Modifier = Modifier
) {
    val videoId = remember(recurso.enlace_contenido) {
        extractYouTubeId(recurso.enlace_contenido)
    }
    val playerState = remember { mutableStateOf<YouTubePlayer?>(null) }

    Column(modifier = modifier) {
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
            onPlayerReady = { player ->
                playerState.value = player
                player.cueVideo(videoId, 0f)
            }
        )
    }
}