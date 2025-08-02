package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import com.plataforma.bienestar.data.api.model.Recurso

@Composable
fun ArticuloContenido(
    recurso: Recurso,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val esEnlace = recurso.enlace_contenido.startsWith("http")

    Column(modifier = modifier.fillMaxWidth()) {
        // Descripción
        if (!recurso.descripcion.isNullOrEmpty()) {
            Text(
                text = recurso.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (esEnlace) {
            Text(
                text = "Enlace al contenido:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = recurso.enlace_contenido,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recurso.enlace_contenido))
                        context.startActivity(intent)
                    }
            )
        } else {
            Text(
                text = recurso.enlace_contenido,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}