package com.plataforma.bienestar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.R
import com.plataforma.bienestar.ui.theme.MainGreen
import com.plataforma.bienestar.ui.theme.BackgroundGreen
import com.plataforma.bienestar.ui.theme.DarkGreen

@Composable
fun BaseScreen(
    content: @Composable (PaddingValues) -> Unit,
    selectedTab: String = "home",
    onTabSelected: (String) -> Unit,
    showNavigationBar: Boolean = true
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
                    .padding(padding)
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
                        selected = selectedTab == "home",
                        onClick = { onTabSelected("home") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Inicio",
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedTab == "home") DarkGreen else Color.White
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "programas",
                        onClick = { onTabSelected("programas") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.programas),
                                contentDescription = "Programas",
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedTab == "programas") DarkGreen else Color.White
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "emociones",
                        onClick = { onTabSelected("emociones") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.emociones),
                                contentDescription = "Emociones",
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedTab == "emociones") DarkGreen else Color.White
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "metas",
                        onClick = { onTabSelected("metas") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.metas),
                                contentDescription = "Metas",
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedTab == "metas") DarkGreen else Color.White
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "perfil",
                        onClick = { onTabSelected("perfil") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.perfil),
                                contentDescription = "Perfil",
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedTab == "perfil") DarkGreen else Color.White
                            )
                        }
                    )
                }
            }
        }
    )
}