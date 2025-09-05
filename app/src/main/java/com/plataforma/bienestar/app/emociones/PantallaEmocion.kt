package com.plataforma.bienestar.app.emociones

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Emocion
import com.plataforma.bienestar.notificaciones.NotificationScheduler
import com.plataforma.bienestar.notificaciones.NotificationUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import com.plataforma.bienestar.util.GestorXP

@Composable
fun PantallaEmociones(
    idUsuario: String,
    tabViewModel: TabViewModel = viewModel()
) {
    val context = LocalContext.current

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

                                            // Xp y Logros
                                            GestorXP.registrarAccionYOtorgarXP(
                                                usuarioId = idUsuario,
                                                evento = "registrar_emocion",
                                                scope = scope
                                            )

                                            // NOTIFICACION MAÑANA 18 PM
                                            NotificationUtils.createNotificationChannel(context)
                                            NotificationScheduler.scheduleAtSixPM(context, idUsuario)

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
    GestorXP.MostrarPopupsAutomaticos()
}