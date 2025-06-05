package com.plataforma.bienestar.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple

@Composable
fun PantallaHome(
    onLogout: () -> Unit,
    userName: String? = null,
    onTabSelected: (String) -> Unit = {} // Nueva función para manejar tabs
) {
    var selectedTab by remember { mutableStateOf("home") } // Estado para el tab seleccionado

    BaseScreen(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            selectedTab = tab
            onTabSelected(tab)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Rectángulo superior (Título)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GrayBlue)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Título Consejo",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White // Color del texto
                    )
                }

                // Rectángulo inferior (Descripción)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightPurple)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Descripción Consejo",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (!userName.isNullOrEmpty()) {
                        "¡Bienvenido, $userName!"
                    } else {
                        "¡Bienvenido!"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onLogout,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaHomePreview() {
    BienestarTheme {
        PantallaHome(
            onLogout = {},
            userName = "Usuario Ejemplo"
        )
    }
}