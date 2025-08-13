package com.plataforma.bienestar.app.emociones

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Emocion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PantallaEmociones(
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

    // Estados para controlar el flujo
    var emocionesUsuario by remember { mutableStateOf<List<Emocion>>(emptyList()) }
    var emocionHoyRegistrada by remember { mutableStateOf<Boolean?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Formateador de fecha para comparar
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val fechaHoy = remember { dateFormat.format(java.util.Date()) }

    // Efecto para comprobar si hay emoción registrada hoy
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val emociones = ApiClient.apiService.getAllEmociones(idUsuario)
                emocionesUsuario = emociones
                // Verificar solo la primera emoción (la más reciente)
                emocionHoyRegistrada = emociones.firstOrNull()?.let { emo ->
                    emo.fechaCreacion?.let { fecha ->
                        dateFormat.format(fecha) == fechaHoy
                    } ?: false
                } ?: false // Si no hay emociones, devuelve false
            } catch (e: Exception) {
                Log.e("Emociones", "Error al obtener emociones: ${e.message}")
                emocionHoyRegistrada = false
            } finally {
                loading = false
            }
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab -> tabViewModel.selectTab(tab) },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when {
                    loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    emocionHoyRegistrada == true -> {
                        Text(
                            text = "Ya registraste tu emoción hoy",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ListaEmociones(emocionesUsuario)
                    }
                    else -> {
                        FormularioEmociones(
                            emocionPrincipal = emocionPrincipal,
                            emocionesSecundarias = emocionesSecundarias,
                            titulo = titulo,
                            descripcion = descripcion,
                            showError = showError,
                            onEmocionPrincipalChange = { emocionPrincipal = it },
                            onEmocionesSecundariasChange = { emocionesSecundarias = it },
                            onTituloChange = { titulo = it },
                            onDescripcionChange = { descripcion = it },
                            onGuardar = {
                                if (emocionPrincipal == null || titulo.isEmpty() || descripcion.isEmpty()) {
                                    showError = true
                                } else {
                                    val emociones = listOfNotNull(emocionPrincipal?.nombre) +
                                            emocionesSecundarias.map { it.nombre }

                                    scope.launch {
                                        try {
                                            // Crear nueva emoción
                                            val nuevaEmocion = ApiClient.apiService.createEmocion(
                                                Emocion(
                                                    titulo = titulo,
                                                    descripcion = descripcion,
                                                    etiquetas = emociones,
                                                    usuario = idUsuario,
                                                    fechaCreacion = java.util.Date()
                                                )
                                            )

                                            // Actualizar la lista localmente sin llamar al backend
                                            emocionesUsuario = listOf(nuevaEmocion) + emocionesUsuario
                                            emocionHoyRegistrada = true

                                        } catch (e: Exception) {
                                            Log.e("Registro Emocion", "Error en backend: ${e.message}")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

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

@Composable
fun EmotionChip(
    text: String,
    selected: Boolean,
    color: Color,
    selectedColor: Color,
    onSelected: () -> Unit
) {
    Surface(
        color = if (selected) selectedColor else color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .border(
                BorderStroke(
                    1.dp,
                    if (selected) color else Color.Gray
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelected)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}

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