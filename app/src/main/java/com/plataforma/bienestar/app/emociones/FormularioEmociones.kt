package com.plataforma.bienestar.app.emociones

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun FormularioEmociones(
    emocionPrincipal: EmocionInfo?,
    emocionesSecundarias: List<EmocionInfo>,
    titulo: String,
    descripcion: String,
    showError: Boolean,
    onEmocionPrincipalChange: (EmocionInfo?) -> Unit,
    onEmocionesSecundariasChange: (List<EmocionInfo>) -> Unit,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Título (pestaña)
        Text(
            text = "¿Cómo te sientes hoy?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de título
        Text(
            text = "Título",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Campo de titulo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            BasicTextField(
                value = titulo,
                onValueChange = onTituloChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(color = Color.Black)
            )
        }

        if (showError && titulo.isEmpty()) {
            Text(
                text = "Debes añadir un título.",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Selector de emoción principal (obligatorio)
        Text(
            text = "Emoción principal",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(EmocionInfo.principales) { emocion ->
                EmotionChip(
                    text = emocion.nombre,
                    selected = emocionPrincipal == emocion,
                    color = emocion.color,
                    selectedColor = emocion.colorSeleccionado,
                    onSelected = {
                        // Solo cambia si es diferente a la actual
                        if (emocionPrincipal != emocion) {
                            onEmocionPrincipalChange(emocion)
                            onEmocionesSecundariasChange(emptyList())
                        }
                    }
                )
            }
        }

        if (showError && emocionPrincipal == null) {
            Text(
                text = "Debes seleccionar una emoción principal",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Selector de emociones secundarias (opcionales)
        Text(
            text = "Otras emociones (opcional)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Filtramos emociones secundarias según la principal seleccionada
        val emocionesSecundariasFiltradas = when (emocionPrincipal) {
            EmocionInfo.Alegria -> EmocionInfo.alegria
            EmocionInfo.Tristeza -> EmocionInfo.tristeza
            EmocionInfo.Ira -> EmocionInfo.ira
            EmocionInfo.Miedo -> EmocionInfo.miedo
            else -> EmocionInfo.todas.filter { it !in EmocionInfo.principales }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(emocionesSecundariasFiltradas) { emocion ->
                EmotionChip(
                    text = emocion.nombre,
                    selected = emocionesSecundarias.contains(emocion),
                    color = emocion.color,
                    selectedColor = emocion.colorSeleccionado,
                    onSelected = {
                        if (emocionPrincipal != null) {
                            val nuevasEmociones = emocionesSecundarias.toMutableList().apply {
                                if (contains(emocion)) {
                                    remove(emocion)
                                } else {
                                    add(emocion)
                                }
                            }
                            onEmocionesSecundariasChange(nuevasEmociones)
                        }
                    }
                )
            }
        }

        // Campo de descripción
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Campo de texto con placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            if (descripcion.isEmpty()) {
                Text(
                    text = "Describe cómo te sientes hoy...",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxSize())
            }
            BasicTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(color = Color.Black)
            )
        }
        if (showError && descripcion.isEmpty()) {
            Text(
                text = "Debes añadir una descripción.",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        // Botón de guardar
        Button(
            onClick = onGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Guardar Emoción")
        }
    }
}