package com.plataforma.bienestar.data.api.model

import java.util.Date

data class Emocion(
    val id: String? = null,
    val titulo: String,
    val descripcion: String,
    val etiquetas: List<String>,
    val usuario: String,
    val fechaCreacion: Date? = null
)
