package com.plataforma.bienestar.data.api.model

data class Recurso(
    val id: String,
    val titulo: String,
    val descripcion: String?,
    val fecha_creacion: String,
    val autor: String?,
    val categoria: String?,
    val etiquetas: List<String>?,
    val duracion: Int?,
    val enlace_contenido: String,
    val tipo: String, // "articulo", "video" o "actividad"
    val dificultad: String? // "baja", "media" o "alta"
)