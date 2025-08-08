package com.plataforma.bienestar.app.emociones

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Emocion
import com.plataforma.bienestar.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaEmociones(
    userName: String? = null,
    idUsuario: String,
    tabViewModel: TabViewModel = viewModel()
) {
    // Corrutinas
    val scope = rememberCoroutineScope()

    // Estados para las emociones
    var emocionPrincipal by remember { mutableStateOf<EmocionInfo?>(null) }
    var emocionesSecundarias by remember { mutableStateOf<List<EmocionInfo>>(emptyList()) }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
                    {
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
                                onValueChange = { titulo = it },
                                modifier = Modifier.fillMaxSize(),
                                textStyle = TextStyle(color = Color.Black)
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
                                    descripcion = emocion.descripcion,
                                    onSelected = {
                                        emocionPrincipal = emocion
                                        showError = false
                                        emocionesSecundarias = emptyList()
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
                                    descripcion = emocion.descripcion,
                                    onSelected = {
                                        emocionesSecundarias = if (emocionesSecundarias.contains(emocion)) {
                                            emocionesSecundarias - emocion
                                        } else {
                                            emocionesSecundarias + emocion
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
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            BasicTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                modifier = Modifier.fillMaxSize(),
                                textStyle = TextStyle(color = Color.Black)
                            )
                        }

                        // Botón de guardar
                        Button(
                            onClick = {
                                if (emocionPrincipal == null || titulo == "" || descripcion == "") {
                                    showError = true
                                } else {
                                    val emociones = listOfNotNull(emocionPrincipal?.nombre) + emocionesSecundarias.map { it.nombre }

                                    scope.launch {
                                        try {
                                            ApiClient.apiService.createEmocion(
                                                Emocion(
                                                    titulo = titulo,
                                                    descripcion = descripcion,
                                                    etiquetas = emociones,
                                                    usuario = idUsuario
                                                )
                                            )
                                            tabViewModel.selectTab("home")
                                        } catch (e: Exception) {
                                            Log.e("Registro Emocion", "Error en backend: ${e.message}")
                                        }
                                    }
                                }
                            },
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
    )
}

@Composable
fun EmotionChip(
    text: String,
    selected: Boolean,
    color: Color,
    selectedColor: Color,
    descripcion: String,
    onSelected: () -> Unit
) {
    Tooltip(text = descripcion, modifier = Modifier.wrapContentSize()) {
        Surface(
            color = if (selected) selectedColor else color,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (selected) color else Color.Gray,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onSelected() }
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (selected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }
    val dismissTimeout = 3000L // 3 segundos

    LaunchedEffect(showTooltip) {
        if (showTooltip) {
            delay(dismissTimeout)
            showTooltip = false
        }
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showTooltip = true },
                    onTap = { showTooltip = false }
                )
            }
    ) {
        content()

        if (showTooltip) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(4.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaEmocionesPreview() {
    BienestarTheme {
        PantallaEmociones(
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}