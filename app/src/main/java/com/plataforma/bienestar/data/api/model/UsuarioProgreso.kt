package com.plataforma.bienestar.data.api.model

data class UsuarioProgreso(
    val nivel: Int,
    val xpNivelActual: Int,
    val xpSiguienteNivel: Int,
    val racha: Int,
    val xpTotal: Int,
    val logros: List<String> = emptyList()
)