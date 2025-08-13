package com.plataforma.bienestar.app.metas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Meta
import com.plataforma.bienestar.ui.theme.BienestarTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMetas(
    idUsuario: String,
    tabViewModel: TabViewModel = viewModel()
) {
    // Corrutinas
    val scope = rememberCoroutineScope()

    // Metas mostradas
    var tipoMetas by remember { mutableStateOf("en_progreso") }

    // Estados para el formulario de meta
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var diasDuracion by remember { mutableStateOf("") }
    var dificultad by remember { mutableStateOf("Media") }

    var showForm by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var metas by remember { mutableStateOf<List<Meta>>(emptyList()) }

    // Llamada a la API cuando se carga la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // Llamada para obtener las metas
            metas = ApiClient.apiService.getMetasActivas(idUsuario)
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar los datos"
            Log.e("PantallaHome", "Error: $error")
        } finally {
            isLoading = false
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Fila de botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón para ver metas completadas
                    Button(
                        onClick = {
                            tipoMetas = "completadas"
                            scope.launch {
                                try {
                                    metas = emptyList()
                                    metas = ApiClient.apiService.getMetasCompletadas(idUsuario)
                                } catch (e: Exception) {
                                    error = "Error al cargar metas completadas: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Completadas")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón para ver metas en progreso
                    Button(
                        onClick = {
                            tipoMetas = "en_progreso"
                            scope.launch {
                                try {
                                    metas = emptyList()
                                    metas = ApiClient.apiService.getMetasActivas(idUsuario)
                                } catch (e: Exception) {
                                    error = "Error al cargar metas canceladas: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("En Progreso")
                    }

                    // Botón para ver metas canceladas
                    Button(
                        onClick = {
                            tipoMetas = "canceladas"
                            scope.launch {
                                try {
                                    metas = emptyList()
                                    metas = ApiClient.apiService.getMetasCanceladas(idUsuario)
                                } catch (e: Exception) {
                                    error = "Error al cargar metas canceladas: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Canceladas")
                    }
                }

                // Botón para mostrar el formulario
                Button(
                    onClick = { showForm = !showForm },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar meta")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Meta")
                }

                // Formulario para crear metas
                if (showForm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            OutlinedTextField(
                                value = titulo,
                                onValueChange = { titulo = it },
                                label = { Text("Título") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Dias de Duracion
                            OutlinedTextField(
                                value = diasDuracion,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d+\$"))) {
                                        diasDuracion = newValue
                                    }
                                },
                                label = { Text("Días de duración") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Selector de dificultad
                            var expandedDificultad by remember { mutableStateOf(false) }
                            val dificultades = listOf("Fácil", "Media", "Difícil")

                            ExposedDropdownMenuBox(
                                expanded = expandedDificultad,
                                onExpandedChange = { expandedDificultad = it }
                            ) {
                                OutlinedTextField(
                                    value = dificultad,
                                    onValueChange = {}, // No se permite edición manual
                                    label = { Text("Dificultad") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(), // Asegura que el menú se ancle correctamente
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDificultad)
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedDificultad,
                                    onDismissRequest = { expandedDificultad = false }
                                ) {
                                    dificultades.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item) },
                                            onClick = {
                                                dificultad = item
                                                expandedDificultad = false
                                                Log.d("Dificultad", "Seleccionado: $item")
                                            }
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    // Validar campos
                                    if (titulo.isEmpty() || descripcion.isEmpty() || diasDuracion.isEmpty()) {
                                        error = "Por favor completa todos los campos"
                                        return@Button
                                    }

                                    // Crear la meta
                                    isLoading = true
                                    try {

                                        // Llamada a la API para crear la meta
                                        scope.launch {
                                            val metaNueva = Meta(
                                                titulo = titulo,
                                                descripcion = descripcion,
                                                diasDuracion = diasDuracion.toInt(),
                                                dificultad = dificultad,
                                                estado = "en_progreso",
                                                usuario = idUsuario
                                            )
                                            try {
                                                ApiClient.apiService.createMeta(metaNueva)

                                                // Limpiar el formulario
                                                titulo = ""
                                                descripcion = ""
                                                diasDuracion = ""
                                                showForm = false
                                                error = null
                                            } catch (e: Exception) {
                                                Log.e("Registro Meta", "Error en backend: ${e.message}")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        error = "Error al crear la meta: ${e.message}"
                                        Log.e("PantallaMetas", error!!)
                                    } finally {
                                        isLoading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White)
                                } else {
                                    Text("Crear Meta")
                                }
                            }

                            error?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
                // Lista de metas
                if (metas.isEmpty()) {
                    // Mensaje cuando no hay metas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val message = when (tipoMetas) {
                            "en_progreso" -> "No tienes metas en progreso. ¡Crea una nueva!"
                            "completadas" -> "No hay metas completadas todavía."
                            "canceladas" -> "No hay metas canceladas."
                            else -> "No hay metas para mostrar."
                        }
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        items(metas) { meta ->
                            MetaItem(
                                meta = meta,
                                onCompleteDay = {
                                    Log.d("meta", "Meta con id : ${meta.id}")
                                    scope.launch {
                                        try {
                                            ApiClient.apiService.incrementarDiasMeta(meta.id!!)
                                        } catch (e: Exception) {
                                            Log.e("Registro Meta", "Error en backend: ${e.message}")
                                        }
                                    }
                                },
                                onCancel = {
                                    scope.launch {
                                        try {
                                            ApiClient.apiService.cancelarMeta(meta.id!!)
                                        } catch (e: Exception) {
                                            Log.e("Registro Meta", "Error en backend: ${e.message}")
                                        }
                                    }
                                },
                                onResume = {
                                    scope.launch {
                                        try {
                                            ApiClient.apiService.reanudarMeta(meta.id!!)
                                        } catch (e: Exception) {
                                            Log.e("Registro Meta", "Error en backend: ${e.message}")
                                        }
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        try {
                                            ApiClient.apiService.eliminarMeta(meta.id!!)
                                        } catch (e: Exception) {
                                            Log.e("Registro Meta", "Error en backend: ${e.message}")
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaMetasPreview() {
    BienestarTheme {
        PantallaMetas(
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}