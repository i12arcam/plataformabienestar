package com.plataforma.bienestar.app.home

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
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.GrayBlue
import java.util.Locale

@Composable
fun PantallaBusqueda(
    parametro: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel(),
    idUsuario: String
) {

    var recursos by remember { mutableStateOf<List<Recurso>>(emptyList()) }
    var estadosRecursos by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    var showError by remember { mutableStateOf(false) }

    val categorias = listOf("Afrontamiento Emocional", "Autoconocimiento", "Autoaceptacion",
        "Desarrollo Personal", "Habilidades Sociales", "Manejo del Estres", "Meditacion",
        "Mindfulness", "Relaciones Saludables", "Respiracion", "Sueño", "Yoga")

    var filtro by remember { mutableStateOf("titulo") }
    var searchText by remember { mutableStateOf(parametro) }


    // Cargar el recurso cuando se abre la pantalla, o cambia el parametro o el filtro
    LaunchedEffect(parametro, filtro) {
        try {
            recursos = ApiClient.apiService.getRecursosBusqueda(parametro, filtro)

            // Obtener estados de todos los recursos
            val estados = mutableMapOf<String, String?>()
            recursos.forEach { recurso ->
                try {
                    val estado = ApiClient.apiService.getEstadoRecurso(idUsuario,recurso.id)
                    estados[recurso.id] = estado.takeIf { it.isNotBlank() } // Solo guarda si no está vacío
                } catch (e: Exception) {
                    Log.e("PantallaHome", "Error al obtener estado del recurso ${recurso.id}: ${e.message}")
                    estados[recurso.id] = null // No hay estado disponible
                }
            }
            estadosRecursos = estados

            Log.d("Recursos", recursos.toString())
        } catch (e: Exception) {
            Log.e("Error recurso","Error al cargar el recurso: ${e.message}")
        }
    }

    // Lista de recursos ordenada
    val recursosOrdenados = remember(recursos, estadosRecursos) {
        recursos.sortedWith(compareBy { recurso ->
            when (estadosRecursos[recurso.id]?.lowercase(Locale.getDefault())) {
                "en_progreso" -> 1
                null -> 2 // Undefined (sin estado)
                "visto" -> 3
                "completado" -> 4
                else -> 5 // Cualquier otro estado no especificado
            }
        })
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
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
                        text = "Resultados búsqueda de $parametro",
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
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                        // Asegurar que el teclado permita caracteres especiales
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            Log.d("Navigation", "Navegando con : $searchText")
                            navController.navigate("busqueda_recursos/$searchText/$idUsuario")
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
                            filtro = "titulo" },
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
                            }},
                        modifier = Modifier.weight(1f)
                    )
                }

                // Lista de recursos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(recursosOrdenados) { recurso ->
                        RecursoItem(
                            recurso = recurso,
                            estado = estadosRecursos[recurso.id],
                            onClick = {
                                Log.d("Navigation", "Navegando con : $recurso")
                                navController.navigate("recurso_detalle/${recurso.id}/${idUsuario}")
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