package com.plataforma.bienestar.gestor.consejos

import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Consejo

suspend fun loadConsejos(
    idUsuario: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: (List<Consejo>) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val consejos = ApiClient.apiServiceGestor.getConsejos(idUsuario)
        onSuccess(consejos)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun createConsejo(
    idUsuario: String,
    consejo: Consejo,
    onLoading: (Boolean) -> Unit,
    onSuccess: (Consejo) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val nuevoConsejo = ApiClient.apiServiceGestor.createConsejo(idUsuario, consejo)
        onSuccess(nuevoConsejo)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun updateConsejo(
    idUsuario: String,
    consejo: Consejo,
    onLoading: (Boolean) -> Unit,
    onSuccess: (Consejo) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        val consejoActualizado = ApiClient.apiServiceGestor.updateConsejo(consejo.id, idUsuario, consejo)
        onSuccess(consejoActualizado)
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}

suspend fun deleteConsejo(
    idUsuario: String,
    consejoId: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    try {
        ApiClient.apiServiceGestor.deleteConsejo(consejoId, idUsuario)
        onSuccess()
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Desconocido"}")
    } finally {
        onLoading(false)
    }
}