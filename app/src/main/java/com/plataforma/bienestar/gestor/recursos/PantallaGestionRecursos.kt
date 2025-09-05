package com.plataforma.bienestar.gestor.recursos

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
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.gestor.BaseScreenGestor
import com.plataforma.bienestar.gestor.ViewModelGestor
import kotlinx.coroutines.launch

@Composable
fun PantallaGestionRecursos(
    idUsuario: String,
    tabViewModel: ViewModelGestor = viewModel()
) {
    var recursos by remember { mutableStateOf<List<Recurso>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Recurso?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Recurso?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar recursos al iniciar
    LaunchedEffect(Unit) {
        loadRecursos(
            idUsuario = idUsuario,
            onLoading = { isLoading = it },
            onSuccess = { recursos = it },
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
                        text = "Gestión de Recursos",
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
                            contentDescription = "Agregar recurso"
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
                                loadRecursos(
                                    idUsuario = idUsuario,
                                    onLoading = { isLoading = it },
                                    onSuccess = { recursos = it },
                                    onError = { error = it }
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Reintentar")
                    }
                } else if (recursos.isEmpty()) {
                    Text(
                        text = "No hay recursos disponibles",
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
                        items(recursos) { recurso ->
                            RecursoItem(
                                recurso = recurso,
                                onEdit = { showEditDialog = recurso },
                                onDelete = { showDeleteDialog = recurso }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo para crear nuevo recurso
    if (showCreateDialog) {
        DialogRecurso(
            titulo = "Crear Recurso",
            onDismiss = { showCreateDialog = false },
            onConfirm = { nuevoRecurso ->
                coroutineScope.launch {
                    createRecurso(
                        idUsuario = idUsuario,
                        recurso = nuevoRecurso,
                        onLoading = { isLoading = it },
                        onSuccess = {
                            recursos = recursos + it
                            showCreateDialog = false
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para editar recurso
    showEditDialog?.let { recurso ->
        DialogRecurso(
            titulo = "Editar Recurso",
            recurso = recurso,
            onDismiss = { showEditDialog = null },
            onConfirm = { recursoActualizado ->
                coroutineScope.launch {
                    updateRecurso(
                        idUsuario = idUsuario,
                        recurso = recursoActualizado,
                        onLoading = { isLoading = it },
                        onSuccess = {
                            recursos = recursos.map { if (it.id == recurso.id) recursoActualizado else it }
                            showEditDialog = null
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para eliminar recurso
    showDeleteDialog?.let { recurso ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Recurso") },
            text = { Text("¿Estás seguro de que quieres eliminar el recurso \"${recurso.titulo}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            deleteRecurso(
                                idUsuario = idUsuario,
                                recursoId = recurso.id,
                                onLoading = { isLoading = it },
                                onSuccess = {
                                    recursos = recursos.filter { it.id != recurso.id }
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