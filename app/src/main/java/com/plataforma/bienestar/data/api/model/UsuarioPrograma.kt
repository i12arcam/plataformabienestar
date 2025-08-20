package com.plataforma.bienestar.data.api.model

import java.util.Date

data class UsuarioPrograma(
    val id: String,
    val usuarioId: String,
    val programaId: String,
    val progreso: Number,
    val estadosRecursos: List<String>,
    val fechaInicio: Date
)
