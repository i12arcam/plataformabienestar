package com.plataforma.bienestar.util

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.LogroResponse
import com.plataforma.bienestar.data.api.model.RegistroAccion
import com.plataforma.bienestar.data.api.model.XpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object GestorXP {

    // Estado interno para controlar popups
    private var mostrarPopup = mutableStateOf(false)
    private var mensajePopup = mutableStateOf("")
    private var tipoPopup = mutableStateOf("") // "nivel" o "logro"

    // Función Principal
    fun registrarAccionYOtorgarXP(
        usuarioId: String,
        evento: String,
        dificultad: String? = null,
        duracion: Int? = null,
        scope: CoroutineScope
    ) {
        scope.launch {
            try {
                val registroAccion = RegistroAccion(
                    usuarioId = usuarioId,
                    evento = evento,
                    dificultad = dificultad,
                    duracion = duracion
                )

                // 1. Registrar la acción
                val logroRespuesta = ApiClient.apiService.registrarAccion(registroAccion)

                // 2. Otorgar XP
                val nivelRespuesta = ApiClient.apiService.otorgarXp(registroAccion)

                // 3. Verificar si hay que mostrar popup
                verificarYMostrarPopup(logroRespuesta,nivelRespuesta)

            } catch (e: Exception) {
                Log.e("GestorXP", "Error: ${e.message}")
            }
        }
    }

    // Función interna que decide si mostrar popup
    private fun verificarYMostrarPopup(logroRespuesta: LogroResponse, nivelRespuesta: XpResponse) {

        Log.d("Popups","$logroRespuesta, $nivelRespuesta")
        if(nivelRespuesta.nuevoNivel) {
            mostrarPopup.value = true
            mensajePopup.value = "¡Subiste al nivel ${nivelRespuesta.nivel}! 🎉"
            tipoPopup.value = "nivel"
        }

        if(logroRespuesta.nuevoLogro) {
            mostrarPopup.value = true
            mensajePopup.value = "¡Conseguiste el logro ${logroRespuesta.logro.titulo}! 🎉"
            tipoPopup.value = "logro"
        }
    }

    // Función para que las pantallas muestren los popups automáticamente
    @Composable
    fun MostrarPopupsAutomaticos() {

        if (mostrarPopup.value) {
            when (tipoPopup.value) {
                "nivel" -> {
                    AlertDialog(
                        onDismissRequest = { mostrarPopup.value = false },
                        title = { Text("¡Felicidades!") },
                        text = { Text(mensajePopup.value) },
                        confirmButton = {
                            Button(onClick = { mostrarPopup.value = false }) {
                                Text("¡Genial!")
                            }
                        }
                    )
                }
                "logro" -> {
                    AlertDialog(
                        onDismissRequest = { mostrarPopup.value = false },
                        title = { Text("🏆 Logro desbloqueado") },
                        text = { Text(mensajePopup.value) },
                        confirmButton = {
                            Button(onClick = { mostrarPopup.value = false }) {
                                Text("¡Vamos!")
                            }
                        }
                    )
                }
            }
        }
    }
}