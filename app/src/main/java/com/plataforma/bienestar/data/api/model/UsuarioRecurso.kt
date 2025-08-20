package com.plataforma.bienestar.data.api.model

import java.util.Date

data class UsuarioRecurso(
    val id: String,
    val usuarioId: String,
    val recursoId: String,
    val estado: String, // "en_progreso" or "completada"
    val fechaInicio: Date
)
