package com.plataforma.bienestar.app.perfil

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.plataforma.bienestar.app.emociones.EmocionInfo
import com.plataforma.bienestar.data.api.model.EmocionGrafica

@Composable
fun GraficaEmociones(
    emociones: List<EmocionGrafica>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                // Configuración básica del gráfico
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                description.isEnabled = false
                setMaxVisibleValueCount(60)
                setPinchZoom(false)
                setDrawGridBackground(false)
                setTouchEnabled(false)

                // Configuración del eje X
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelCount = emociones.size
                    axisMinimum = -0.5f
                    axisMaximum = emociones.size.toFloat()
                }

                // Configuración del eje Y
                axisLeft.apply {
                    setLabelCount(5, true)
                    axisMinimum = 0f
                    setDrawGridLines(true)
                    gridLineWidth = 0.5f
                    granularity = 1f
                }

                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            // Ordenar emociones según el orden definido
            val emocionesOrdenadas = emociones.sortedBy { emocion ->
                EmocionInfo.principales.indexOfFirst { it.nombre == emocion.tipo }
            }

            // Preparar entradas de datos
            val entries = emocionesOrdenadas.mapIndexed { index, emocion ->
                BarEntry(index.toFloat(), emocion.count.toFloat())
            }

            // Preparar etiquetas
            val labels = emocionesOrdenadas.map { it.tipo }

            // Preparar colores
            val colors = emocionesOrdenadas.map { emocion ->
                when (emocion.tipo) {
                    EmocionInfo.Alegria.nombre -> EmocionInfo.verdeOscuro.toArgb()
                    EmocionInfo.Tristeza.nombre -> EmocionInfo.azulOscuro.toArgb()
                    EmocionInfo.Miedo.nombre -> EmocionInfo.purpuraOscuro.toArgb()
                    EmocionInfo.Ira.nombre -> EmocionInfo.rojoOscuro.toArgb()
                    else -> EmocionInfo.verdeOscuro.toArgb()
                }
            }

            val dataSet = BarDataSet(entries, "Emociones").apply {
                setColors(colors)
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 12f
                setDrawValues(true)
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }
            chart.invalidate()
            chart.animateY(1000)
        },
        modifier = modifier
    )
}