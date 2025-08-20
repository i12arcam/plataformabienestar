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
import com.plataforma.bienestar.app.programas.ProgramaItem
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.UsuarioPrograma
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistorialProgramas(
    idUsuario: String,
    estado: String, // "completado", "en_progreso"
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val usuarioProgramas = remember { mutableStateListOf<UsuarioPrograma>() }
    val programasData = remember { mutableStateMapOf<String, Programa>() }
    val isLoading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar los UsuarioProgramas
    LaunchedEffect(idUsuario, estado) {
        isLoading.value = true
        error.value = null

        try {
            val response = ApiClient.apiService.getHistorialProgramas(idUsuario, estado)
            Log.d("Historial Programas","$response")
            usuarioProgramas.clear()
            usuarioProgramas.addAll(response)

            // Cargar los detalles de cada programa
            response.forEach { usuarioPrograma ->
                scope.launch {
                    try {
                        Log.d("IdPrograma","IdPrograma: ${usuarioPrograma.programaId}")
                        val programa = ApiClient.apiService.getPrograma(usuarioPrograma.programaId)
                        programasData[usuarioPrograma.programaId] = programa
                    } catch (e: Exception) {
                        Log.e("HistorialProgramas", "Error al cargar programa ${usuarioPrograma.programaId}: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            error.value = "Error al cargar programas: ${e.message}"
            Log.e("HistorialProgramas", error.value!!)
        } finally {
            isLoading.value = false
        }
    }

    Column(modifier = modifier) {
        Text(
            text = when(estado) {
                "completado" -> "Programas completados"
                "en_progreso" -> "Programas en progreso"
                else -> "Historial de programas"
            },
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading.value && usuarioProgramas.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        } else if (error.value != null) {
            Text(
                text = error.value!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else if (usuarioProgramas.isEmpty()) {
            Text(
                text = "No hay programas en este estado",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarioProgramas) { usuarioPrograma ->
                    val programa = programasData[usuarioPrograma.programaId]

                    if (programa != null) {
                        // Card que envuelve al ProgramaItem y muestra la fecha
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
                                // Fecha en la parte superior
                                val dateFormatter = remember {
                                    SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
                                }

                                Text(
                                    text = dateFormatter.format(usuarioPrograma.fechaInicio),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    textAlign = TextAlign.End
                                )

                                // Divider
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                // El ProgramaItem original
                                ProgramaItem(
                                    programa = programa,
                                    usuarioPrograma = usuarioPrograma,
                                    onClick = {
                                        Log.d("Navigation", "Navegando a programa: ${programa.id}")
                                        navController.navigate("programa_detalle/${programa.id}/${idUsuario}")
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