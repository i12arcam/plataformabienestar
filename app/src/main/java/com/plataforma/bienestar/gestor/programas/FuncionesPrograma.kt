package com.plataforma.bienestar.gestor.programas

import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.ProgramaNuevo  // ← Importar el nuevo modelo

suspend fun createPrograma(
    idUsuario: String,
    programa: ProgramaNuevo,  // ← Cambiado a ProgramaNuevo
    onLoading: (Boolean) -> Unit,
    onSuccess: (Programa) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val nuevoPrograma = ApiClient.apiServiceGestor.createPrograma(idUsuario, programa)
        onSuccess(nuevoPrograma)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun updatePrograma(
    idUsuario: String,
    programa: ProgramaNuevo,  // ← Cambiado a ProgramaNuevo
    onLoading: (Boolean) -> Unit,
    onSuccess: (Programa) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val programaActualizado = ApiClient.apiServiceGestor.updatePrograma(programa.id, idUsuario, programa)
        onSuccess(programaActualizado)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

// deletePrograma se mantiene igual
suspend fun deletePrograma(
    idUsuario: String,
    programaId: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        ApiClient.apiServiceGestor.deletePrograma(programaId, idUsuario)
        onSuccess()
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}