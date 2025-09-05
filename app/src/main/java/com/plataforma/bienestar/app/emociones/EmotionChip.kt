package com.plataforma.bienestar.app.emociones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EmotionChip(
    text: String,
    selected: Boolean,
    color: Color,
    selectedColor: Color,
    onSelected: () -> Unit
) {
    Surface(
        color = if (selected) selectedColor else color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .border(
                BorderStroke(
                    1.dp,
                    if (selected) color else Color.Gray
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}