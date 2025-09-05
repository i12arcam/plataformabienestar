package com.plataforma.bienestar.data.api.model

data class RegistroAccion(
    val usuarioId: String,
    val evento: String,
    val dificultad: String?,
    val duracion: Number?
)
