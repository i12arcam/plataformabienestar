package com.plataforma.bienestar.data.api.model

import java.util.Date

data class UsuarioActividad(
    val id: String,
    val usuario_id: String,
    val recurso_id: String,
    val estado: String, // "en_progreso" or "completada"
    val fecha_inicio: Date,
    val fecha_finalizacion: Date? = null
)
