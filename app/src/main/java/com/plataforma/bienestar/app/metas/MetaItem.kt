package com.plataforma.bienestar.app.metas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Meta
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MetaItem(
    meta: Meta,
    onCompleteDay: () -> Unit,
    onCancel: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados locales
    var localDiasCompletados by remember { mutableIntStateOf(meta.diasCompletados) }
    var localEstado by remember { mutableStateOf(meta.estado) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Para bloquear los botones
    var isButtonLocked by remember { mutableStateOf(false) }

    // Formato para comparar solo la parte de fecha (sin hora)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val hoy = dateFormat.format(Date())

    // Para comprobar si ya se completó el dia
    val yaSeCompletoHoy = localDiasCompletados != 0 &&
            (meta.fechaActualizacion?.let { fecha ->
                dateFormat.format(fecha) == hoy
            } ?: false)

    // Estado combinado para los botones
    val botonBloqueado = yaSeCompletoHoy || localEstado.lowercase() != "en_progreso" || isButtonLocked

    // Calculamos el progreso
    val progress = if (meta.diasDuracion > 0) {
        localDiasCompletados.toFloat() / meta.diasDuracion.toFloat()
    } else {
        0f
    }

    // Color basado en la dificultad
    val difficultyColor = when (meta.dificultad.lowercase()) {
        "fácil" -> Color.Green
        "difícil" -> Color.Red
        else -> Color(0xFFFFA500) // Naranja para media
    }

    // Función para manejar el click en "Completar Día"
    fun handleCompleteDay() {
        if (!botonBloqueado) {
            isButtonLocked = true
            if (localDiasCompletados < meta.diasDuracion) {
                localDiasCompletados += 1
                if (localDiasCompletados >= meta.diasDuracion) {
                    localEstado = "completada"
                }
            }
            onCompleteDay()
        }
    }

    // Función para manejar el click en "Cancelar"
    fun handleCancel() {
        localEstado = "cancelada"
        onCancel()
    }

    // Función para manejar el click en "Reanudar"
    fun handleResume() {
        localEstado = "en_progreso"
        onResume()
    }

    // Función para manejar el click en "Borrar"
    fun handleDelete() {
        showDeleteDialog = true
    }

    // Diálogo de confirmación para borrar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar meta definitivamente") },
            text = { Text("¿Estás seguro de que quieres eliminar esta meta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            // Fila superior con título y categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meta.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                // Dificultad
                Text(
                    text = meta.dificultad,
                    style = MaterialTheme.typography.labelSmall,
                    color = difficultyColor,
                    modifier = Modifier
                        .background(
                            color = difficultyColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción
            Text(
                text = meta.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progreso personalizada (solo para metas en progreso o completadas)
            if (localEstado.lowercase() != "cancelada") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Fila de información inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Días completados (no mostrar para metas canceladas)
                if (localEstado.lowercase() != "cancelada") {
                    Text(
                        text = "$localDiasCompletados/${meta.diasDuracion} días",
                        style = MaterialTheme.typography.labelMedium
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Fila para el estado y los botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Estado
                Text(
                    text = when (localEstado.lowercase()) {
                        "completada" -> "✅ Completada"
                        "cancelada" -> "❌ Cancelada"
                        else -> "🔄 En progreso"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Fila para los botones
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    when (localEstado.lowercase()) {
                        "en_progreso" -> {
                            // Botón para cancelar
                            Box(
                                modifier = Modifier
                                    .clickable { handleCancel() }
                                    .background(
                                        color = Color.Red,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Cancelar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón para completar día
                            Box(
                                modifier = Modifier
                                    .clickable(enabled = !botonBloqueado) { handleCompleteDay() }
                                    .background(
                                        color = if (botonBloqueado) MaterialTheme.colorScheme.surfaceVariant
                                        else MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (botonBloqueado) "Día Completado" else "Completar Día",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (botonBloqueado) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        "cancelada" -> {
                            // Botón para reanudar
                            Box(
                                modifier = Modifier
                                    .clickable { handleResume() }
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Reanudar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón para borrar
                            Box(
                                modifier = Modifier
                                    .clickable { handleDelete() }
                                    .background(
                                        color = Color.Red,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Borrar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                        "completada" -> {
                            // Botón para borrar (opcional para metas completadas)
                            Box(
                                modifier = Modifier
                                    .clickable { handleDelete() }
                                    .background(
                                        color = Color.Red,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Borrar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MetaItemPreview() {
    MaterialTheme {
        Column {
            MetaItem(
                meta = Meta(
                    id = "1",
                    titulo = "Ejercicio diario",
                    descripcion = "Hacer 30 minutos de ejercicio cada día",
                    diasDuracion = 30,
                    diasCompletados = 12,
                    dificultad = "Media",
                    estado = "en_progreso",
                    usuario = "123",
                    fechaActualizacion = Date()
                ),
                onCompleteDay = {},
                onCancel = {},
                onResume = {},
                onDelete = {}
            )
        }
    }
}