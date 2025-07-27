package com.plataforma.bienestar.data.repository

import com.plataforma.bienestar.data.api.ApiService
import com.plataforma.bienestar.data.api.model.Usuario
import com.plataforma.bienestar.data.local.PreferencesManager

// Inutil
class UsuarioRepository(
    private val apiService: ApiService
) {
    suspend fun createUser(firebaseId: String, name: String, email: String): Result<Unit> {
        return try {
            val user = Usuario(firebaseId, name, email)
            val response = apiService.createUser(user)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Puedes añadir más métodos aquí para otras operaciones
}