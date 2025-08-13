package com.plataforma.bienestar.app.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple

@Composable
fun PantallaHome(
    idUsuario: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }

    // Estados para el consejo
    var consejoNombre by remember { mutableStateOf("Título Consejo") }
    var consejoContenido by remember { mutableStateOf("Descripción Consejo") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Estado para los recursos
    var recursos by remember { mutableStateOf<List<Recurso>>(emptyList()) }

    Log.d("IDUsuario", "IDUsuario: $idUsuario")

    // Llamada a la API cuando se carga la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val consejo = ApiClient.apiService.getConsejo(idUsuario)
            consejoNombre = consejo.titulo
            consejoContenido = consejo.contenido

            // Llamada para obtener recursos
            recursos = ApiClient.apiService.getRecursosSeleccionados(idUsuario)
            Log.d("Recursos", "Recursos: $recursos")
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar los datos"
            Log.e("PantallaHome", "Error: $error")
        } finally {
            isLoading = false
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
                            contentDescription = "Buscar"
                        )
                    },
                    placeholder = {
                        Text(text = "Buscar contenido...")
                    },
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            Log.d("Navigation", "Navegando con : $searchText")
                            if(searchText != ""){
                                navController.navigate("busqueda_recursos/$searchText")
                            }

                        }
                    )
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (error != null) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Rectángulo superior (Título)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GrayBlue)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = consejoNombre,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }

                    // Rectángulo inferior (Descripción)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp) // Altura máxima antes de hacer scroll
                            .background(LightPurple)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()) // Habilita el scroll vertical
                    ) {
                        Text(
                            text = consejoContenido,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Lista de recursos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(recursos) { recurso ->
                            RecursoItem(
                                recurso = recurso,
                                onClick = {
                                    Log.d("Navigation", "Navegando con : $recurso y $idUsuario")
                                    navController.navigate("recurso_detalle/${recurso.id}/${idUsuario}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaHomePreview() {
    BienestarTheme {
        val navController = rememberNavController()
        PantallaHome(
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1",
            navController = navController
        )
    }
}