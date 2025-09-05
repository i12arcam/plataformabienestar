package com.plataforma.bienestar.gestor.programas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.gestor.BaseScreenGestor
import com.plataforma.bienestar.gestor.ViewModelGestor
import kotlinx.coroutines.launch

@Composable
fun PantallaGestionProgramas(
    idUsuario: String,
    tabViewModel: ViewModelGestor = viewModel()
) {
    var programas by remember { mutableStateOf<List<Programa>>(emptyList()) }
    var recursosDisponibles by remember { mutableStateOf<List<Recurso>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Programa?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Programa?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar programas y recursos al iniciar
    LaunchedEffect(Unit) {
        loadProgramasYRecursos(
            idUsuario = idUsuario,
            onLoading = { isLoading = it },
            onSuccessProgramas = { programas = it },
            onSuccessRecursos = { recursosDisponibles = it },
            onError = { error = it }
        )
    }

    BaseScreenGestor(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp)
            ) {
                // Header con título y botón de agregar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestión de Programas",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar programa"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loadProgramasYRecursos(
                                    idUsuario = idUsuario,
                                    onLoading = { isLoading = it },
                                    onSuccessProgramas = { programas = it },
                                    onSuccessRecursos = { recursosDisponibles = it },
                                    onError = { error = it }
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Reintentar")
                    }
                } else if (programas.isEmpty()) {
                    Text(
                        text = "No hay programas disponibles",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(programas) { programa ->
                            ProgramaItem(
                                programa = programa,
                                onEdit = { showEditDialog = programa },
                                onDelete = { showDeleteDialog = programa }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo para crear nuevo programa
    if (showCreateDialog) {
        DialogPrograma(
            titulo = "Crear Programa",
            recursosDisponibles = recursosDisponibles,
            onDismiss = { showCreateDialog = false },
            onConfirm = { nuevoPrograma ->  // ← Ahora recibe ProgramaNuevo
                coroutineScope.launch {
                    createPrograma(
                        idUsuario = idUsuario,
                        programa = nuevoPrograma,  // ← Pasar ProgramaNuevo
                        onLoading = { isLoading = it },
                        onSuccess = {
                            programas = programas + it
                            showCreateDialog = false
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para editar programa
    showEditDialog?.let { programa ->
        DialogPrograma(
            titulo = "Editar Programa",
            programa = programa,
            recursosDisponibles = recursosDisponibles,
            onDismiss = { showEditDialog = null },
            onConfirm = { programaActualizado ->  // ← Ahora recibe ProgramaNuevo
                coroutineScope.launch {
                    updatePrograma(
                        idUsuario = idUsuario,
                        programa = programaActualizado,  // ← Pasar ProgramaNuevo
                        onLoading = { isLoading = it },
                        onSuccess = {
                            programas = programas.map { if (it.id == programa.id) it else programa }
                            showEditDialog = null
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para eliminar programa
    showDeleteDialog?.let { programa ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Programa") },
            text = { Text("¿Estás seguro de que quieres eliminar el programa \"${programa.titulo}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            deletePrograma(
                                idUsuario = idUsuario,
                                programaId = programa.id,
                                onLoading = { isLoading = it },
                                onSuccess = {
                                    programas = programas.filter { it.id != programa.id }
                                    showDeleteDialog = null
                                },
                                onError = { error = it }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función para cargar programas y recursos
private suspend fun loadProgramasYRecursos(
    idUsuario: String,
    onLoading: (Boolean) -> Unit,
    onSuccessProgramas: (List<Programa>) -> Unit,
    onSuccessRecursos: (List<Recurso>) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val programas = ApiClient.apiServiceGestor.getProgramas(idUsuario)
        val recursos = ApiClient.apiServiceGestor.getRecursos(idUsuario)
        onSuccessProgramas(programas)
        onSuccessRecursos(recursos)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}