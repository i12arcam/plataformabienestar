package com.plataforma.bienestar.app.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Recurso
import java.util.Locale

@Composable
fun RecursoItem(
    recurso: Recurso,
    estado: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = recurso.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            recurso.descripcion?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = recurso.tipo.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall
                    )

                    recurso.duracion?.let {
                        Text(
                            text = "Duración: $it min",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    recurso.dificultad?.let {
                        Text(
                            text = "Dificultad: $it",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                estado?.let { estadoValue ->
                    val (textoEstado, colorEstado) = when(estadoValue.lowercase(Locale.getDefault())) {
                        "completado" -> "Completado" to Color.Green
                        "visto" -> "Visto" to Color.Blue
                        "en_progreso" -> "En progreso" to Color.Gray
                        else -> estadoValue to MaterialTheme.colorScheme.onSurface
                    }

                    Text(
                        text = textoEstado,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorEstado,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}