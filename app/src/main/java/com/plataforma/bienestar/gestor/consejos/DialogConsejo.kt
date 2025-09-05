package com.plataforma.bienestar.gestor.consejos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Consejo

@Composable
fun DialogConsejo(
    titulo: String,
    consejo: Consejo? = null,
    onDismiss: () -> Unit,
    onConfirm: (Consejo) -> Unit
) {
    var tituloText by remember { mutableStateOf(consejo?.titulo ?: "") }
    var contenidoText by remember { mutableStateOf(consejo?.contenido ?: "") }
    var etiquetasText by remember { mutableStateOf(consejo?.etiquetas?.joinToString(",") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column {
                OutlinedTextField(
                    value = tituloText,
                    onValueChange = { tituloText = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contenidoText,
                    onValueChange = { contenidoText = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

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

                    val nuevoConsejo = Consejo(
                        id = consejo?.id ?: "", // Para nuevos consejos, el id se generará en el backend
                        titulo = tituloText,
                        contenido = contenidoText,
                        etiquetas = etiquetas
                    )
                    onConfirm(nuevoConsejo)
                },
                enabled = tituloText.isNotBlank() && contenidoText.isNotBlank()
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