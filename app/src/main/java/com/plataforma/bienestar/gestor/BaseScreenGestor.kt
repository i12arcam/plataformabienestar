package com.plataforma.bienestar.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.ui.theme.MainGreen
import com.plataforma.bienestar.ui.theme.BackgroundGreen
import com.plataforma.bienestar.ui.theme.DarkGreen

@Composable
fun BaseScreenGestor(
    content: @Composable (PaddingValues) -> Unit,
    selectedTab: String = "perfil",
    onTabSelected: (String) -> Unit,
    showNavigationBar: Boolean = true,
) {
    Scaffold(
        // --- 1. TOP BAR ---
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MainGreen,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center // Centrado absoluto
                ) {

                    // Título (centrado absolutamente)
                    Text(
                        text = "MindAura",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },

        // --- 2. CONTENIDO ---
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGreen)
            ) {
                content(padding)
            }
        },

        // --- 3. BOTTOM BAR ---
        bottomBar = {
            if(showNavigationBar) {
                NavigationBar(
                    containerColor = MainGreen,
                    modifier = Modifier.height(56.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == "consejos",
                        onClick = { onTabSelected("consejos") },
                        icon = {
                            // Usamos solo texto en lugar de icono
                            Text(
                                text = "Consejos",
                                color = if (selectedTab == "consejos") DarkGreen else Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "recursos",
                        onClick = { onTabSelected("recursos") },
                        icon = {
                            Text(
                                text = "Recursos",
                                color = if (selectedTab == "recursos") DarkGreen else Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "programas",
                        onClick = { onTabSelected("programas") },
                        icon = {
                            Text(
                                text = "Programas",
                                color = if (selectedTab == "programas") DarkGreen else Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "perfil",
                        onClick = { onTabSelected("perfil") },
                        icon = {
                            Text(
                                text = "Perfil",
                                color = if (selectedTab == "perfil") DarkGreen else Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    )
}