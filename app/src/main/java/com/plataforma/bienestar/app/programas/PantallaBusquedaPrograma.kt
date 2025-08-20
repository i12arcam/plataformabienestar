package com.plataforma.bienestar.app.programas

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plataforma.bienestar.R
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.UsuarioPrograma
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.GrayBlue

@Composable
fun PantallaBusquedaPrograma(
    parametro: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel(),
    idUsuario: String
) {
    var coincidencias by remember { mutableStateOf<List<Programa>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    val categorias = listOf("Afrontamiento emocional", "Autoconocimiento", "Autoaceptacion",
        "Desarrollo personal", "Habilidades sociales", "Manejo del Estres", "Meditacion",
        "Mindfulness", "Relaciones saludables", "Respiracion", "Sueño", "Yoga")

    var filtro by remember { mutableStateOf("titulo") }
    var searchText by remember { mutableStateOf(parametro) }

    // Cargar programas cuando se abre la pantalla, o cambia el parametro o el filtro
    LaunchedEffect(parametro, filtro) {
        isLoading = true
        try {
            coincidencias = ApiClient.apiService.getProgramasBusqueda(parametro, filtro)
            Log.d("Programas", coincidencias.toString())
        } catch (e: Exception) {
            error = "Error al cargar los programas: ${e.message}"
            Log.e("BusquedaPrograma", error!!)
        } finally {
            isLoading = false
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        showNavigationBar = false,
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "",
                        tint = DarkGreen,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(vertical = 20.dp, horizontal = 5.dp)
                            .size(24.dp)
                    )
                    // Resultados Busqueda Titulo
                    Text(
                        text = "Resultados búsqueda de '$parametro'",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray,
                    )
                }

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
                        Text(text = "Buscar programas...")
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
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                        autoCorrect = true,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            Log.d("Navigation", "Navegando con: $searchText")
                            navController.navigate("busqueda_programas/$searchText/$idUsuario")
                        }
                    )
                )

                if(showError) {
                    Log.d("Showerror","Mostrando error")
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Categoría no válida",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Opciones válidas: ${categorias.joinToString(", ")}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

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
                        onClick = {
                            showError = false
                            filtro = "titulo"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = "Categoría",
                        isSelected = filtro == "categoria",
                        onClick = {
                            val normalizedParam = searchText.trim().lowercase()
                            val isCategoryValid = categorias.any { it.trim().lowercase() == normalizedParam }
                            if(isCategoryValid){
                                Log.d("Showerror","False")
                                filtro = "categoria"
                                showError = false
                            } else {
                                Log.d("Showerror","True")
                                showError = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

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
                    coincidencias.isEmpty() -> {
                        Text(
                            text = "No se encontraron programas",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            items(coincidencias) { programa ->
                                var usuarioPrograma: UsuarioPrograma? = null
                                LaunchedEffect(programa.id) {
                                    try {
                                        usuarioPrograma = ApiClient.apiService.obtenerUsuarioPrograma(idUsuario, programa.id)
                                    } catch (e: Exception) {
                                        Log.d("No encontrado", "No se pudo cargar UsuarioPrograma para ${programa.id}")
                                    }
                                }

                                ProgramaItem(
                                    programa = programa,
                                    usuarioPrograma = usuarioPrograma,
                                    onClick = {
                                        navController.navigate("programa_detalle/${programa.id}/$idUsuario")
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
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