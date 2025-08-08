package com.plataforma.bienestar.app.emociones

import androidx.compose.ui.graphics.Color

sealed class EmocionInfo(
    val nombre: String,
    val color: Color,
    val colorSeleccionado: Color,
    val descripcion: String
) {
    companion object {
        // Paleta de colores
        // Alegría (Verdes)
        val verdeOscuro = Color(0xFF2E7D32)
        val verde = Color(0xFF4CAF50)
        val verdeClaro = Color(0xFF81C784)

        // Tristeza (Azules)
        val azulOscuro = Color(0xFF1565C0)
        val azul = Color(0xFF2196F3)
        val azulClaro = Color(0xFF64B5F6)

        // Miedo (Púrpuras)
        val purpuraOscuro = Color(0xFF7B1FA2)
        val purpura = Color(0xFF9C27B0)
        val purpuraClaro = Color(0xFFBA68C8)

        // Ira (Rojos)
        val rojoOscuro = Color(0xFFD32F2F)
        val rojo = Color(0xFFF44336)
        val rojoClaro = Color(0xFFE57373)

        // Listas organizadas
        val principales by lazy { listOf(Alegria, Tristeza, Miedo, Ira) }

        val alegria by lazy { listOf(Amor, Gratitud, Esperanza, Euforia, Orgullo) }
        val tristeza by lazy { listOf(Culpa, Verguenza, Desesperanza, Melancolia, Soledad, Aburrimiento, Envidia) }
        val miedo by lazy { listOf(Ansiedad, Temor, Terror, Celos) }
        val ira by lazy { listOf(Frustracion, Indignacion, Desprecio, CulpaIra, EnvidiaIra) }

        val todas by lazy { principales + alegria + tristeza + miedo + ira }
    }

    // Emociones principales
    object Alegria : EmocionInfo("Alegría", verde, verdeOscuro, "Alegria")
    object Tristeza : EmocionInfo("Tristeza", azul, azulOscuro, "Tristeza")
    object Miedo : EmocionInfo("Miedo", purpura, purpuraOscuro, "Miedo")
    object Ira : EmocionInfo("Ira", rojo, rojoOscuro, "Ira")

    // Emociones secundarias de Alegría
    object Amor : EmocionInfo("Amor", verdeClaro, verde, "Afecto profundo")
    object Gratitud : EmocionInfo("Gratitud", verdeClaro, verde, "Aprecio por lo recibido")
    object Esperanza : EmocionInfo("Esperanza", verdeClaro, verde, "Expectativa positiva")
    object Euforia : EmocionInfo("Euforia", verdeClaro, verde, "Alegría exaltada")
    object Orgullo : EmocionInfo("Orgullo", verdeClaro, verde, "Satisfacción por logro")

    // Emociones secundarias de Tristeza
    object Culpa : EmocionInfo("Culpa", azulClaro, azul, "Remordimiento")
    object Verguenza : EmocionInfo("Vergüenza", azulClaro, azul, "Malestar por error")
    object Desesperanza : EmocionInfo("Desesperanza", azulClaro, azul, "Rendición o agotamiento")
    object Melancolia : EmocionInfo("Melancolía", azulClaro, azul, "Tristeza suave, persistente, nostálgica")
    object Soledad : EmocionInfo("Soledad", azulClaro, azul, "Falta de compañía")
    object Aburrimiento : EmocionInfo("Aburrimiento", azulClaro, azul, "Falta de interés")
    object Envidia : EmocionInfo("Envidia", azulClaro, azul, "Tristeza por lo que otros tienen")

    // Emociones secundarias de Miedo
    object Ansiedad : EmocionInfo("Ansiedad", purpuraClaro, purpura, "Preocupación constante")
    object Temor : EmocionInfo("Temor", purpuraClaro, purpura, "Miedo anticipado, preocupación")
    object Terror : EmocionInfo("Terror", purpuraClaro, purpura, "Miedo extremo, horror")
    object Celos : EmocionInfo("Celos", purpuraClaro, purpura, "Miedo a perder algo")

    // Emociones secundarias de Ira
    object Frustracion : EmocionInfo("Frustración", rojoClaro, rojo, "Enojo por obstáculos")
    object Indignacion : EmocionInfo("Indignación", rojoClaro, rojo, "Ira por injusticia")
    object Desprecio : EmocionInfo("Desprecio", rojoClaro, rojo, "Menosprecio a algo/alguien")
    object CulpaIra : EmocionInfo("Culpa", rojoClaro, rojo, "Remordimiento con uno mismo")
    object EnvidiaIra : EmocionInfo("Envidia", rojoClaro, rojo, "Deseo mezclado con resentimiento")
}