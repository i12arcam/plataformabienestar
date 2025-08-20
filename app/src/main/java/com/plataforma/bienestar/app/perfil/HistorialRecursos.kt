package com.plataforma.bienestar.app.perfil

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.plataforma.bienestar.app.home.RecursoItem
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.data.api.model.UsuarioRecurso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistorialRecursos(
    idUsuario: String,
    estado: String, // "completado", "visto", "en_progreso"
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val usuarioRecursos = remember { mutableStateListOf<UsuarioRecurso>() }
    val recursosData = remember { mutableStateMapOf<String, Recurso>() }
    val isLoading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar los UsuarioRecursos
    LaunchedEffect(idUsuario, estado) {
        isLoading.value = true
        error.value = null

        try {
            val response = ApiClient.apiService.getHistorialRecursos(idUsuario, estado)
            usuarioRecursos.clear()
            usuarioRecursos.addAll(response)

            // Cargar los detalles de cada recurso
            response.forEach { usuarioRecurso ->
                scope.launch {
                    try {
                        val recurso = ApiClient.apiService.getRecurso(usuarioRecurso.recursoId)
                        recursosData[usuarioRecurso.recursoId] = recurso
                    } catch (e: Exception) {
                        Log.e("Historial", "Error al cargar recurso ${usuarioRecurso.recursoId}: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            error.value = "Error al cargar recursos: ${e.message}"
            Log.e("Historial", error.value!!)
        } finally {
            isLoading.value = false
        }
    }

    Column(modifier = modifier) {
        Text(
            text = when(estado) {
                "completado" -> "Recursos completados"
                "visto" -> "Recursos vistos"
                "en_progreso" -> "Recursos en progreso"
                else -> "Historial"
            },
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading.value && usuarioRecursos.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        } else if (error.value != null) {
            Text(
                text = error.value!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else if (usuarioRecursos.isEmpty()) {
            Text(
                text = "No hay recursos en este estado",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarioRecursos) { usuarioRecurso ->
                    val recurso = recursosData[usuarioRecurso.recursoId]

                    if (recurso != null) {
                        // Card que envuelve al RecursoItem y muestra la fecha
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                val formattedDate = remember(usuarioRecurso.fechaInicio) {
                                    try {
                                        // Parse the original date string
                                        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                        val date = inputFormat.parse(usuarioRecurso.fechaInicio.toString())

                                        // Format to desired output
                                        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
                                        outputFormat.format(date!!)
                                    } catch (e: Exception) {
                                        Log.e("Historial", "Error parsing date: ${e.message}")
                                        usuarioRecurso.fechaInicio.toString() // Fallback to original string
                                    }
                                }

                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    textAlign = TextAlign.End
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                RecursoItem(
                                    recurso = recurso,
                                    estado = usuarioRecurso.estado,
                                    onClick = {
                                        Log.d("Navigation", "Navegando con: $recurso y $idUsuario")
                                        navController.navigate("recurso_detalle/${recurso.id}/${idUsuario}")
                                    }
                                )
                            }
                        }
                    } else {
                        // Placeholder mientras carga
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}