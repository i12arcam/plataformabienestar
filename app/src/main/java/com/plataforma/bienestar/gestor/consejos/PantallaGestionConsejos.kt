package com.plataforma.bienestar.gestor.consejos

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
import com.plataforma.bienestar.data.api.model.Consejo
import com.plataforma.bienestar.gestor.BaseScreenGestor
import com.plataforma.bienestar.gestor.ViewModelGestor
import kotlinx.coroutines.launch

@Composable
fun PantallaGestorConsejos(
    idUsuario: String,
    tabViewModel: ViewModelGestor = viewModel()
) {
    var consejos by remember { mutableStateOf<List<Consejo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Consejo?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Consejo?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar consejos al iniciar
    LaunchedEffect(Unit) {
        loadConsejos(
            idUsuario = idUsuario,
            onLoading = { isLoading = it },
            onSuccess = { consejos = it },
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
                        text = "Gestión de Consejos",
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
                            contentDescription = "Agregar consejo"
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
                                loadConsejos(
                                    idUsuario = idUsuario,
                                    onLoading = { isLoading = it },
                                    onSuccess = { consejos = it },
                                    onError = { error = it }
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Reintentar")
                    }
                } else if (consejos.isEmpty()) {
                    Text(
                        text = "No hay consejos disponibles",
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
                        items(consejos) { consejo ->
                            ConsejoItem(
                                consejo = consejo,
                                onEdit = { showEditDialog = consejo },
                                onDelete = { showDeleteDialog = consejo }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo para crear nuevo consejo
    if (showCreateDialog) {
        DialogConsejo(
            titulo = "Crear Consejo",
            onDismiss = { showCreateDialog = false },
            onConfirm = { nuevoConsejo ->
                coroutineScope.launch {
                    createConsejo(
                        idUsuario = idUsuario,
                        consejo = nuevoConsejo,
                        onLoading = { isLoading = it },
                        onSuccess = {
                            consejos = consejos + it
                            showCreateDialog = false
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para editar consejo
    showEditDialog?.let { consejo ->
        DialogConsejo(
            titulo = "Editar Consejo",
            consejo = consejo,
            onDismiss = { showEditDialog = null },
            onConfirm = { consejoActualizado ->
                coroutineScope.launch {
                    updateConsejo(
                        idUsuario = idUsuario,
                        consejo = consejoActualizado,
                        onLoading = { isLoading = it },
                        onSuccess = {
                            consejos = consejos.map { if (it.id == consejo.id) consejoActualizado else it }
                            showEditDialog = null
                        },
                        onError = { error = it }
                    )
                }
            }
        )
    }

    // Diálogo para eliminar consejo
    showDeleteDialog?.let { consejo ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Consejo") },
            text = { Text("¿Estás seguro de que quieres eliminar el consejo \"${consejo.titulo}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            deleteConsejo(
                                idUsuario = idUsuario,
                                consejoId = consejo.id,
                                onLoading = { isLoading = it },
                                onSuccess = {
                                    consejos = consejos.filter { it.id != consejo.id }
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