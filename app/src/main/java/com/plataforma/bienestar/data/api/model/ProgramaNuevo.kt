package com.plataforma.bienestar.data.api.model

data class ProgramaNuevo(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val etiquetas: List<String>,
    var recursos: List<String>
)