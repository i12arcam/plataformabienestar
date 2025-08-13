package com.plataforma.bienestar.data.api.model

import java.util.Date

data class Meta(
    val id: String? = null,  // ID opcional para cuando se crea nueva meta
    val titulo: String,
    val descripcion: String,
    val diasDuracion: Int,  // Duración en días
    val diasCompletados: Int = 0,  // Progreso (días completados)
    val dificultad: String,
    val estado: String,
    val fechaInicio: Date = Date(),  // Por defecto la fecha actual
    val fechaFin: Date? = null,  // Opcional hasta que se complete
    val usuario: String,  // Referencia al usuario
    val fechaActualizacion: Date? = null
)

