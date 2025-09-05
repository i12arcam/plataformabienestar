package com.plataforma.bienestar.gestor.recursos

import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso

suspend fun loadRecursos(
    idUsuario: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: (List<Recurso>) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val recursos = ApiClient.apiServiceGestor.getRecursos(idUsuario)
        onSuccess(recursos)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun createRecurso(
    idUsuario: String,
    recurso: Recurso,
    onLoading: (Boolean) -> Unit,
    onSuccess: (Recurso) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val nuevoRecurso = ApiClient.apiServiceGestor.createRecurso(idUsuario, recurso)
        onSuccess(nuevoRecurso)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun updateRecurso(
    idUsuario: String,
    recurso: Recurso,
    onLoading: (Boolean) -> Unit,
    onSuccess: (Recurso) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val recursoActualizado = ApiClient.apiServiceGestor.updateRecurso(recurso.id, idUsuario, recurso)
        onSuccess(recursoActualizado)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun deleteRecurso(
    idUsuario: String,
    recursoId: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        ApiClient.apiServiceGestor.deleteRecurso(recursoId, idUsuario)
        onSuccess()
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}