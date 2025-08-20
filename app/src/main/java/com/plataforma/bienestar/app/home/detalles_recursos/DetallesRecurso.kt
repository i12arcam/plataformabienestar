package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.plataforma.bienestar.R
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple
import java.util.Locale

@Composable
fun DetallesRecurso(
    usuarioId: String,
    recurso: Recurso?,
    isLoading: Boolean,
    error: String?,
    navController: NavController,
    estaEnPrograma : Boolean,
    onBackClick: (() -> Unit)? = null,
    estado: String? = null,
    cambiarEstadoActividadPrograma: (nuevoEstado: String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp)
            )
        } else if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else if (recurso != null) {
            // Rectángulo superior (Título del recurso)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrayBlue)
            ) {
                Row {
                    // Mostrar flecha solo si onBackClick no es null
                    if (onBackClick != null) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Volver atrás",
                            tint = DarkGreen,
                            modifier = Modifier
                                .clickable(onClick = onBackClick)
                                .padding(vertical = 20.dp, horizontal = 5.dp)
                                .size(24.dp)
                        )
                    } else {
                        // Espaciador para mantener la alineación cuando no hay flecha
                        Spacer(modifier = Modifier.size(24.dp))
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = recurso.titulo,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            recurso.autor?.let { autor ->
                                Text(
                                    text = "Por $autor",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            estado?.let { estadoValue ->
                                val (textoEstado, colorEstado) = when(estadoValue.lowercase(Locale.getDefault())) {
                                    "completado" -> "Completado" to Color.Green
                                    "visto" -> "Visto" to Color.Blue
                                    "en_progreso" -> "En progreso" to Color.Yellow
                                    "no-iniciado" -> "Sin iniciar" to Color.White
                                    else -> estadoValue to MaterialTheme.colorScheme.onSurface
                                }

                                Text(
                                    text = textoEstado,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorEstado,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .background(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Contenido del recurso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightPurple)
                    .padding(16.dp)
            ) {
                when (recurso.tipo) {
                    "articulo" -> ArticuloContenido(recurso, usuarioId,estaEnPrograma,cambiarEstadoActividadPrograma)
                    "video" -> VideoContenido(recurso, usuarioId,estaEnPrograma,cambiarEstadoActividadPrograma)
                    "actividad" -> ActividadContenido(usuarioId, recurso, navController,estaEnPrograma,cambiarEstadoActividadPrograma)
                    else -> Text("Tipo de recurso no soportado: ${recurso.tipo}")
                }
            }
        }
    }
}