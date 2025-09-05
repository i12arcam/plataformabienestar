package com.plataforma.bienestar.app.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.model.Logro
import java.util.Locale
import com.plataforma.bienestar.R

// Composable para mostrar los logros en una cuadrícula
@Composable
fun GridLogros(
    logros: List<Logro>,
    modifier: Modifier = Modifier
) {
    // Agregar una altura máxima para evitar el conflicto de scroll
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.heightIn(max = 400.dp), // LIMITAR LA ALTURA MÁXIMA
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logros) { logro ->
            InsigniaLogro(
                logro = logro,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

// Composable para mostrar una insignia de logro individual
@Composable
fun InsigniaLogro(
    logro: Logro,
    modifier: Modifier = Modifier
) {
    val icono = when (logro.evento) {
        "registrar_emocion" -> R.drawable.ic_emocion
        "visualizar_articulo" -> R.drawable.ic_articulo
        "visualizar_video" -> R.drawable.ic_video
        "completar_actividad" -> R.drawable.ic_actividad
        "iniciar_sesion" -> R.drawable.ic_sesion
        "completar_programa" -> R.drawable.ic_programa
        "incrementar_meta" -> R.drawable.ic_programa
        else -> R.drawable.ic_emocion
    }

    val colorBorde = when (logro.rareza) {
        "HIERRO" -> Color.Gray
        "BRONCE" -> Color(0xFFCD7F32) // Bronze
        "PLATA" -> Color.LightGray
        "ORO" -> Color(0xFFFFD700) // Gold
        "PLATINO" -> Color(0xFFE5E4E2) // Platinum
        "DIAMANTE" -> Color(0xFFB9F2FF) // Diamond
        else -> Color.Gray
    }

    val colorFondo = when (logro.rareza) {
        "HIERRO" -> Color(0xFFA9A9A9).copy(alpha = 0.2f)
        "BRONCE" -> Color(0xFFCD7F32).copy(alpha = 0.1f)
        "PLATA" -> Color(0xFFC0C0C0).copy(alpha = 0.1f)
        "ORO" -> Color(0xFFFFD700).copy(alpha = 0.1f)
        "PLATINO" -> Color(0xFFE5E4E2).copy(alpha = 0.1f)
        "DIAMANTE" -> Color(0xFFB9F2FF).copy(alpha = 0.1f)
        else -> Color.Gray.copy(alpha = 0.1f)
    }

    Card(
        modifier = modifier,
        border = BorderStroke(2.dp, colorBorde)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = icono),
                    contentDescription = logro.titulo,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = logro.titulo,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = logro.rareza.lowercase(Locale.ROOT)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    style = MaterialTheme.typography.bodySmall,
                    color = colorBorde
                )
            }
        }
    }
}