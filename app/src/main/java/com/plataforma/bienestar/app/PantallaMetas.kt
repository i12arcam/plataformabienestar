package com.plataforma.bienestar.app

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Emocion
import com.plataforma.bienestar.data.api.model.Meta
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMetas(
    userName: String? = null,
    idUsuario: String,
    tabViewModel: TabViewModel = viewModel()
) {
    // Corrutinas
    val scope = rememberCoroutineScope()

    // Estados para el formulario de meta
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var diasDuracion by remember { mutableStateOf("") }
    var dificultad by remember { mutableStateOf("Media") }
    var estado by remember { mutableStateOf("En progreso") }
    var showForm by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Botón para mostrar el formulario
                Button(
                    onClick = { showForm = !showForm },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Crear Nueva Meta",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

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

                            OutlinedTextField(
                                value = categoria,
                                onValueChange = { categoria = it },
                                label = { Text("Categoría") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = diasDuracion,
                                onValueChange = { diasDuracion = it },
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
                                    onValueChange = {},
                                    label = { Text("Dificultad") },
                                    modifier = Modifier.fillMaxWidth(),
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
                                            }
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    // Validar campos
                                    if (titulo.isEmpty() || descripcion.isEmpty() || categoria.isEmpty() || diasDuracion.isEmpty()) {
                                        error = "Por favor completa todos los campos"
                                        return@Button
                                    }

                                    // Crear la meta
                                    isLoading = true
                                    try {

                                        // Llamada a la API para crear la meta
                                        scope.launch {
                                            try {
                                                ApiClient.apiService.createMeta(
                                                    Meta(
                                                        titulo = titulo,
                                                        descripcion = descripcion,
                                                        categoria = categoria,
                                                        diasDuracion = diasDuracion.toInt(),
                                                        dificultad = dificultad,
                                                        estado = "en_progreso",
                                                        usuarioId = idUsuario
                                                    )
                                                )
                                                tabViewModel.selectTab("home")
                                            } catch (e: Exception) {
                                                Log.e("Registro Meta", "Error en backend: ${e.message}")
                                            }
                                        }

                                        // Limpiar el formulario
                                        titulo = ""
                                        descripcion = ""
                                        categoria = ""
                                        diasDuracion = ""
                                        showForm = false
                                        error = null
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
                                    Text("Guardar Meta")
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
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaMetasPreview() {
    BienestarTheme {
        PantallaMetas(
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}