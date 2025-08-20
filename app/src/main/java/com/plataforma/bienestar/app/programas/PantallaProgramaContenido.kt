package com.plataforma.bienestar.app.programas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plataforma.bienestar.R
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.app.home.RecursoItem
import com.plataforma.bienestar.app.home.detalles_recursos.DetallesRecurso
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.data.api.model.UsuarioPrograma
import com.plataforma.bienestar.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@Composable
fun PantallaProgramaContenido(
    usuarioId: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel(),
    programaId: String
) {
    // Estados para manejar la carga del programa
    var programa by remember { mutableStateOf<Programa?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var usuarioPrograma by remember { mutableStateOf<UsuarioPrograma?>(null) }
    var iniciandoPrograma by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar el programa cuando se abre la pantalla o cambia el ID
    LaunchedEffect(programaId) {
        isLoading = true
        error = null
        try {
            programa = ApiClient.apiService.getPrograma(programaId)
            try {
                usuarioPrograma = ApiClient.apiService.obtenerUsuarioPrograma(usuarioId, programaId)
            } catch (e: Exception) {
                // No mostramos error si no se encuentra la relación usuario-programa
                Log.d("Programa", "No se encontró relación usuario-programa: ${e.message}")
                usuarioPrograma = null
            }
        } catch (e: Exception) {
            error = "Error al cargar el programa: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        showNavigationBar = false,
        content = { _ ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                programa == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontró el programa",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Encabezado del programa
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(16.dp)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                    contentDescription = "Volver atrás",
                                    tint = DarkGreen,
                                    modifier = Modifier
                                        .clickable(onClick = { navController.popBackStack() })
                                        .padding(vertical = 20.dp, horizontal = 5.dp)
                                        .size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = programa?.titulo ?: "",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = programa?.descripcion ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )

                                    if (usuarioPrograma == null) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    iniciandoPrograma = true
                                                    try {
                                                        Log.d("Size","Size: ${programa!!.recursos.size}")
                                                        usuarioPrograma = ApiClient.apiService.iniciarPrograma(usuarioId, programaId,
                                                            programa!!.recursos.size)
                                                    } catch (e: Exception) {
                                                        error = "Error al iniciar el programa: ${e.message}"
                                                    } finally {
                                                        iniciandoPrograma = false
                                                    }
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.End),
                                            enabled = !iniciandoPrograma
                                        ) {
                                            if (iniciandoPrograma) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text("Iniciar Programa")
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Iniciado el: ${usuarioPrograma?.fechaInicio}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        // Lista de recursos del programa
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(programa?.recursos ?: emptyList()) { recursoPrograma ->
                                // Obtener el índice del recurso actual
                                val index = programa?.recursos?.indexOf(recursoPrograma) ?: 0

                                var recurso by remember { mutableStateOf<Recurso?>(null) }
                                var isLoadingRecurso by remember { mutableStateOf(true) }
                                var errorRecurso by remember { mutableStateOf<String?>(null) }

                                LaunchedEffect(recursoPrograma.id) {
                                    isLoadingRecurso = true
                                    try {
                                        recurso = ApiClient.apiService.getRecurso(recursoPrograma.id)
                                    } catch (e: Exception) {
                                        errorRecurso = "Error al cargar el recurso: ${e.message}"
                                    } finally {
                                        isLoadingRecurso = false
                                    }
                                }

                                if (usuarioPrograma != null) {
                                    // Verificar que el índice es válido para estadosRecursos
                                    val estado = if (usuarioPrograma!!.estadosRecursos.size > index) {
                                        usuarioPrograma!!.estadosRecursos[index]
                                    } else {
                                        null
                                    }

                                    DetallesRecurso(
                                        usuarioId = usuarioId,
                                        recurso = recurso,
                                        isLoading = isLoadingRecurso,
                                        error = errorRecurso,
                                        navController = navController,
                                        estaEnPrograma = true,
                                        estado = estado,
                                        cambiarEstadoActividadPrograma = { nuevoEstado ->
                                            coroutineScope.launch {
                                                try {
                                                    Log.d("Size","Size: ${programa!!.recursos.size}")
                                                    usuarioPrograma = ApiClient.apiService.actualizarEstadoRecursoPrograma(
                                                        usuarioId = usuarioId,
                                                        programaId = programaId,
                                                        posicion = index,
                                                        estado = nuevoEstado
                                                    )
                                                } catch (e: Exception) {
                                                    error = "Error al actualizar el recurso: ${e.message}"
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    recurso?.let {
                                        RecursoItem(
                                            recurso = it,
                                            estado = null,
                                            onClick = {}
                                        )
                                    } ?: run {
                                        if (isLoadingRecurso) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                            )
                                        } else if (errorRecurso != null) {
                                            Text(
                                                text = "Error al cargar el recurso",
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    )
}