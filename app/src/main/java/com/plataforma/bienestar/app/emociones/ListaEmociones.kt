package com.plataforma.bienestar.app.emociones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Emocion
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ListaEmociones(emociones: List<Emocion>, modifier: Modifier = Modifier) {
    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("es","ES"))
    }

    LazyColumn(modifier = modifier) {
        items(emociones) { emocion ->
            // Obtener la emoción principal (primera de la lista)
            val emocionPrincipalNombre = emocion.etiquetas.firstOrNull()
            val emocionPrincipalInfo = EmocionInfo.todas.find { it.nombre == emocionPrincipalNombre }

            // Determinar el color de fondo según la emoción principal
            val backgroundColor = when (emocionPrincipalInfo) {
                EmocionInfo.Alegria -> Color(0xFF81C784) // verdeClaro
                EmocionInfo.Tristeza -> Color(0xFF64B5F6) // azulClaro
                EmocionInfo.Miedo -> Color(0xFFBA68C8) // purpuraClaro
                EmocionInfo.Ira -> Color(0xFFE57373) // rojoClaro
                else -> MaterialTheme.colorScheme.surface // Color por defecto
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Fecha en la parte superior derecha
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = emocion.titulo,
                            style = MaterialTheme.typography.titleLarge
                        )

                        emocion.fechaCreacion?.let { fecha ->
                            Text(
                                text = dateFormatter.format(fecha),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Text(
                        text = emocion.descripcion,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(emocion.etiquetas) { etiqueta ->
                            val emocionInfo = EmocionInfo.todas.find { it.nombre == etiqueta }

                            Surface(
                                modifier = Modifier.padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = emocionInfo?.colorSeleccionado ?: MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = etiqueta,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}