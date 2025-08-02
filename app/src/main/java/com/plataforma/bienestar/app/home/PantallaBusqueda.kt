package com.plataforma.bienestar.app.home

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.GrayBlue

@Composable
fun PantallaBusqueda(
    parametro: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel()
) {

    var coincidencias by remember { mutableStateOf<List<Recurso>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var filtro by remember { mutableStateOf("titulo") }
    var searchText by remember { mutableStateOf(parametro) }


    // Cargar el recurso cuando se abre la pantalla o cambia el ID o el filtro
    LaunchedEffect(parametro, filtro) {
        isLoading = true
        try {
            coincidencias = ApiClient.apiService.getRecursosBusqueda(parametro, filtro)
        } catch (e: Exception) {
            error = "Error al cargar el recurso: ${e.message}"
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
                            Log.d("Navigation", "Navegando con : ${searchText}")
                            navController.navigate("busqueda_recursos/$searchText")
                        }
                    )
                )
                // Filtros
                Text(
                    text = "Filtrar por",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterButton(
                        text = "Título",
                        isSelected = filtro == "titulo",
                        onClick = { filtro = "titulo" },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = "Etiqueta",
                        isSelected = filtro == "etiqueta",
                        onClick = { filtro = "etiqueta" },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = "Categoría",
                        isSelected = filtro == "categoria",
                        onClick = { filtro = "categoria" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Lista de recursos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(coincidencias) { recurso ->
                        RecursoItem(
                            recurso = recurso,
                            onClick = {
                                Log.d("Navigation", "Navegando con : ${recurso}")
                                navController.navigate("recurso_detalle/${recurso.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) DarkGreen else GrayBlue,
            contentColor = Color.White
        ),
        modifier = modifier
    ) {
        Text(text = text)
    }
}