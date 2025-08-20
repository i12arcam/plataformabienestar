package com.plataforma.bienestar.data.api.model

data class EmocionesHistorialResponse(
    val emociones: List<EmocionGrafica>,
    val periodo: Periodo
)

data class EmocionGrafica(
    val tipo: String,
    val count: Int
)
data class Periodo(
    val inicio: String,
    val fin: String
)
