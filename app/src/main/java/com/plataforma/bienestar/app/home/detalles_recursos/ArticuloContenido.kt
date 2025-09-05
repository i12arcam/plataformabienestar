package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.plataforma.bienestar.util.GestorXP

@Composable
fun ArticuloContenido(
    recurso: Recurso,
    usuarioId: String,
    estaEnPrograma: Boolean,
    cambiarEstadoActividadPrograma: (nuevoEstado: String) -> Unit,

    ) {
    val context = LocalContext.current
    val esEnlace = recurso.enlace_contenido.startsWith("http")
    val coroutineScope = rememberCoroutineScope()

    // Actualiza el estado del recurso para que aparezca como Visto
    fun establecerRecursoVisto() {
        coroutineScope.launch {
            try {
                if (!estaEnPrograma) { // No está en programa, se setea a Visto
                    ApiClient.apiService.setRecursoVisto(usuarioId, recurso.id)

                    // Xp y Logros
                    GestorXP.registrarAccionYOtorgarXP(
                        usuarioId = usuarioId,
                        evento = "visualizar_articulo",
                        dificultad = recurso.dificultad,
                        duracion = recurso.duracion,
                        scope = coroutineScope
                    )

                } else { // Está en programa. No se setea a visto para el recurso, sino completado para el programa
                    cambiarEstadoActividadPrograma("completado")
                }

            } catch (e: Exception) {
                // Manejar el error si es necesario
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Descripción
        if (!recurso.descripcion.isNullOrEmpty()) {
            Text(
                text = recurso.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (esEnlace) {
            // Contenido para enlaces externos
            Button(
                onClick = {
                    establecerRecursoVisto()
                    val intent = Intent(Intent.ACTION_VIEW, recurso.enlace_contenido.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Abrir Enlace")
            }
        } else {
            // Contenido para texto plano
            Text(
                text = recurso.enlace_contenido,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { establecerRecursoVisto() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Cargar Contenido")
            }
        }
    }
}