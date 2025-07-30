package com.plataforma.bienestar.data.api.model

data class Emocion(
    val titulo: String,
    val descripcion: String,
    val etiquetas: List<String>,
    val usuario: String
)
