package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Recurso

@Composable
fun VideoContenido(
    recurso: Recurso,
    modifier: Modifier = Modifier
) {
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
                    .aspectRatio(16f / 9f)
            )
        }
    }
}