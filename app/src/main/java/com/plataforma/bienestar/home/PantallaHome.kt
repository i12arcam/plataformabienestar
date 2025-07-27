package com.plataforma.bienestar.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple

@Composable
fun PantallaHome(
    onLogout: () -> Unit,
    userName: String? = null,
    onTabSelected: (String) -> Unit = {},
    idUsuario: String
) {
    var selectedTab by remember { mutableStateOf("home") }
    var searchText by remember { mutableStateOf("") }

    // Estados para el consejo
    var consejoNombre by remember { mutableStateOf("Título Consejo") }
    var consejoContenido by remember { mutableStateOf("Descripción Consejo") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Llamada a la API cuando se carga la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val consejo = ApiClient.apiService.getConsejo(idUsuario)
            consejoNombre = consejo.titulo
            consejoContenido = consejo.contenido
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar el consejo"
            Log.e("PantallaHome", "Error al obtener consejo: $error")
        } finally {
            isLoading = false
        }
    }

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
                // Barra de búsqueda
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .heightIn(min = 56.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    placeholder = {
                        Text(text = "Buscar contenido...")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (error != null) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Rectángulo superior (Título)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GrayBlue)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = consejoNombre,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
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
                            text = consejoContenido,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}