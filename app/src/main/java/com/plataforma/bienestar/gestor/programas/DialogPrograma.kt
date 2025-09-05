package com.plataforma.bienestar.gestor.programas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.ProgramaNuevo
import com.plataforma.bienestar.data.api.model.Recurso

@Composable
fun DialogPrograma(
    titulo: String,
    programa: Programa? = null,
    recursosDisponibles: List<Recurso>,
    onDismiss: () -> Unit,
    onConfirm: (ProgramaNuevo) -> Unit
) {
    var tituloText by remember { mutableStateOf(programa?.titulo ?: "") }
    var descripcionText by remember { mutableStateOf(programa?.descripcion ?: "") }
    var categoriaText by remember { mutableStateOf(programa?.categoria ?: "") }
    var etiquetasText by remember { mutableStateOf(programa?.etiquetas?.joinToString(",") ?: "") }
    var recursosSeleccionados by remember { mutableStateOf(programa?.recursos?.map { it.id }?.toSet() ?: emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = tituloText,
                    onValueChange = { tituloText = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcionText,
                    onValueChange = { descripcionText = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoriaText,
                    onValueChange = { categoriaText = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de recursos
                Text(
                    text = "Recursos incluidos:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    recursosDisponibles.forEach { recurso ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = recursosSeleccionados.contains(recurso.id),
                                onCheckedChange = { isChecked ->
                                    recursosSeleccionados = if (isChecked) {
                                        recursosSeleccionados + recurso.id
                                    } else {
                                        recursosSeleccionados - recurso.id
                                    }
                                }
                            )
                            Text(
                                text = recurso.titulo,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = etiquetasText,
                    onValueChange = { etiquetasText = it },
                    label = { Text("Etiquetas (separadas por coma)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val etiquetas = etiquetasText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    // SOLUCIÓN: Usar ProgramaNuevo con solo los IDs
                    val recursosIds = recursosDisponibles
                        .filter { it.id in recursosSeleccionados }
                        .map { it.id }

                    val nuevoPrograma = ProgramaNuevo(  //
                        id = programa?.id ?: "",
                        titulo = tituloText,
                        descripcion = descripcionText,
                        categoria = categoriaText,
                        etiquetas = etiquetas,
                        recursos = recursosIds
                    )
                    onConfirm(nuevoPrograma)
                },
                enabled = tituloText.isNotBlank() && descripcionText.isNotBlank() && categoriaText.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}