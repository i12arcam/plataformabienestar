package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.plataforma.bienestar.data.api.model.Recurso

enum class EstadoActividad {
    NO_INICIADA,
    EN_PROGRESO,
    TERMINADA
}

@Composable
fun ActividadContenido(
    recurso: Recurso,
    navController: NavController,
    modifier: Modifier = Modifier,
    onEstadoCambiado: (EstadoActividad) -> Unit = {}
) {
    var estadoActividad by remember { mutableStateOf(EstadoActividad.NO_INICIADA) }
    val playerState = remember { mutableStateOf<YouTubePlayer?>(null) }
    var playerRef by playerState

    val videoId = remember(recurso.enlace_contenido) {
        extractYouTubeId(recurso.enlace_contenido ?: "")
    }

    DisposableEffect(Unit) {
        onDispose {
            playerRef = null
        }
    }

    Column(modifier = modifier) {
        if (!recurso.descripcion.isNullOrBlank()) {
            Text(
                text = recurso.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        recurso.enlace_contenido?.let { videoUrl ->
            VideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                onPlayerReady = { player ->
                    playerRef = player
                    player.cueVideo(videoId, 0f)
                },
                onVideoEnded = {
                    estadoActividad = EstadoActividad.TERMINADA
                    onEstadoCambiado(EstadoActividad.TERMINADA)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (estadoActividad) {
            EstadoActividad.NO_INICIADA -> {
                Button(
                    onClick = {
                        playerRef?.loadVideo(videoId, 0f)
                        estadoActividad = EstadoActividad.EN_PROGRESO
                        onEstadoCambiado(EstadoActividad.EN_PROGRESO)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Actividad")
                }
            }

            EstadoActividad.EN_PROGRESO -> {
                // Mostramos un botón deshabilitado con indicador de progreso
                Button(
                    onClick = { /* No acción durante progreso */ },
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("En Progreso")
                }
            }

            EstadoActividad.TERMINADA -> {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Terminar Actividad")
                }
            }
        }
    }
}