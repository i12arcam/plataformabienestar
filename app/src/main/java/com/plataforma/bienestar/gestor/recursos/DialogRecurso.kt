package com.plataforma.bienestar.gestor.recursos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Recurso

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogRecurso(
    titulo: String,
    recurso: Recurso? = null,
    onDismiss: () -> Unit,
    onConfirm: (Recurso) -> Unit
) {
    val categoriaInicial = remember(recurso?.categoria) {
        if (recurso?.categoria != null) {
            val categorias = listOf(
                "Afrontamiento Emocional", "Autoconocimiento", "Autoaceptacion",
                "Desarrollo Personal", "Habilidades Sociales", "Manejo del Estres", "Meditacion",
                "Mindfulness", "Relaciones Saludables", "Respiracion", "Sueño", "Yoga"
            )
            categorias.find { categoria ->
                categoria.lowercase()
                    .replace(" ", "")
                    .replace("á", "a")
                    .replace("é", "e")
                    .replace("í", "i")
                    .replace("ó", "o")
                    .replace("ú", "u") == recurso.categoria
            } ?: recurso.categoria
        } else {
            ""
        }
    }

    var tituloText by remember { mutableStateOf(recurso?.titulo ?: "") }
    var descripcionText by remember { mutableStateOf(recurso?.descripcion ?: "") }
    var enlaceText by remember { mutableStateOf(recurso?.enlace_contenido ?: "") }
    var tipoText by remember { mutableStateOf(recurso?.tipo ?: "articulo") }
    var dificultadText by remember { mutableStateOf(recurso?.dificultad ?: "media") }
    var etiquetasText by remember { mutableStateOf(recurso?.etiquetas?.joinToString(",") ?: "") }
    var duracionText by remember { mutableStateOf(recurso?.duracion?.toString() ?: "") }
    var categoriaText by remember { mutableStateOf(categoriaInicial) }

    val categorias = listOf(
        "Afrontamiento Emocional", "Autoconocimiento", "Autoaceptacion",
        "Desarrollo Personal", "Habilidades Sociales", "Manejo del Estres", "Meditacion",
        "Mindfulness", "Relaciones Saludables", "Respiracion", "Sueño", "Yoga"
    )

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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = enlaceText,
                    onValueChange = { enlaceText = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = duracionText,
                        onValueChange = { duracionText = it },
                        label = { Text("Duración (min)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Selector de tipo
                    var expandedTipo by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = tipoText.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            label = { Text("Tipo") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expandedTipo = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Abrir menú"
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            listOf("articulo", "video", "actividad").forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        tipoText = tipo
                                        expandedTipo = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de dificultad
                var expandedDificultad by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = dificultadText.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("Dificultad") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedDificultad = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedDificultad,
                        onDismissRequest = { expandedDificultad = false }
                    ) {
                        listOf("baja", "media", "alta").forEach { dificultad ->
                            DropdownMenuItem(
                                text = { Text(dificultad.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    dificultadText = dificultad
                                    expandedDificultad = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de categoría
                var expandedCategoria by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = categoriaText,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedCategoria = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaText = categoria
                                    expandedCategoria = false
                                }
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

                    // Normalizar la categoría: quitar tildes y espacios
                    val categoriaNormalizada = categoriaText
                        .lowercase()
                        .replace(" ", "")
                        .replace("á", "a")
                        .replace("é", "e")
                        .replace("í", "i")
                        .replace("ó", "o")
                        .replace("ú", "u")
                        .ifBlank { null }

                    val nuevoRecurso = Recurso(
                        id = recurso?.id ?: "",
                        titulo = tituloText,
                        descripcion = descripcionText.ifBlank { null },
                        fecha_creacion = recurso?.fecha_creacion ?: "",
                        autor = recurso?.autor,
                        categoria = categoriaNormalizada,
                        etiquetas = etiquetas.ifEmpty { null },
                        duracion = duracionText.toIntOrNull(),
                        enlace_contenido = enlaceText,
                        tipo = tipoText,
                        dificultad = dificultadText
                    )
                    onConfirm(nuevoRecurso)
                },
                enabled = tituloText.isNotBlank() && enlaceText.isNotBlank()
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