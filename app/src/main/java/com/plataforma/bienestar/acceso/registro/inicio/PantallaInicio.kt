package com.plataforma.bienestar.acceso.registro.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plataforma.bienestar.R
import com.plataforma.bienestar.ui.theme.BackgroundGreen
import com.plataforma.bienestar.ui.theme.Black
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.Green

@Preview
@Composable
fun PantallaInicio(
    navigateToLogin: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.mindauralogo),
            contentDescription = "",
            modifier = Modifier.
                clip(CircleShape)
                .size(140.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Conecta contigo mismo",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Supérate", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navigateToSignUp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BackgroundGreen)
        ) {
            Text(text = "Registrarse", color = Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            modifier = Modifier.clickable(onClick = onGoogleSignInClick),
            painterResource(id = R.drawable.googleicon),
            "Continuar con Google"
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            Modifier.clickable { },
            painterResource(id = R.drawable.googleicon),
            "Continuar con Facebook"
        )
        Text(
            text = "Iniciar Sesión",
            color = Color.White,
            modifier = Modifier
                .padding(24.dp)
                .clickable {navigateToLogin() },
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CustomButton(modifier: Modifier, painter: Painter, title: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
            .background(Green)
            .border(2.dp, Black, CircleShape),

        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 16.dp)
                .size(16.dp)
        )
        Text(
            text = title,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}