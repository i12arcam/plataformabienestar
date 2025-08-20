package com.plataforma.bienestar.app.programas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.UsuarioPrograma
import com.plataforma.bienestar.ui.theme.BienestarTheme

@Composable
fun PantallaProgramas(
    idUsuario: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var programas by remember { mutableStateOf<List<Programa>>(emptyList()) }

        // Llamada a la API para obtener programas
        LaunchedEffect(Unit) {
            isLoading = true
            try {
                programas = ApiClient.apiService.getProgramasSeleccionados(idUsuario)
                Log.d("PantallaProgramas", "Programas cargados: $programas")
            } catch (e: Exception) {
                error = e.message ?: "Error al cargar los programas"
                Log.e("PantallaProgramas", "Error: $error")
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
                        .padding(bottom = 16.dp)
                ) {
                    // Barra de búsqueda (idéntica a PantallaHome)
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .heightIn(min = 56.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar programas"
                            )
                        },
                        placeholder = { Text("Buscar programas...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchText.isNotEmpty()) {
                                    navController.navigate("busqueda_programas/$searchText/$idUsuario")
                                }
                            }
                        )
                    )

                    // Contenido principal
                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        error != null -> {
                            Text(
                                text = error!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        programas.isEmpty() -> {
                            Text(
                                text = "No hay programas disponibles",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(programas) { programa ->
                                    // Ejecuta la carga solo si no está ya en el mapa
                                    var usuarioPrograma by remember {
                                        mutableStateOf<UsuarioPrograma?>(
                                            null
                                        )
                                    }
                                    LaunchedEffect(programa.id) {
                                        try {
                                            usuarioPrograma =
                                                ApiClient.apiService.obtenerUsuarioPrograma(
                                                    idUsuario,
                                                    programa.id
                                                )
                                        } catch (e: Exception) {
                                            Log.d("Error", "No encontrado: ${programa.id}")
                                        }
                                    }
                                    // Obtiene el valor actual del mapa
                                    ProgramaItem(
                                        programa = programa,
                                        usuarioPrograma = usuarioPrograma,
                                        onClick = { navController.navigate("programa_detalle/${programa.id}/$idUsuario") }
                                    )
                                    Spacer(Modifier.height(8.dp))
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
    fun PantallaProgramasPreview() {
        BienestarTheme {
            val navController = rememberNavController()
            PantallaProgramas(
                idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1",
                navController = navController
            )
        }
    }