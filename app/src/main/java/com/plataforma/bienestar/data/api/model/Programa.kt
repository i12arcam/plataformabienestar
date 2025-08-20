package com.plataforma.bienestar.data.api.model

data class Programa(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val etiquetas: List<String>,
    var recursos: List<Recurso>
)