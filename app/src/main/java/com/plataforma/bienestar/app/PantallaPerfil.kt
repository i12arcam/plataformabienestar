package com.plataforma.bienestar.app

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.app.home.PantallaHome
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple

@Composable
fun PantallaPerfil(
    onLogout: () -> Unit,
    userName: String? = null,
    idUsuario: String,
    tabViewModel: TabViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }

    // Estados para el consejo
    var consejoNombre by remember { mutableStateOf("Título Emocion") }
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
        selectedTab = tabViewModel.selectedTab.value, // Usa el valor del ViewModel
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab) // Actualiza a través del ViewModel
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

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
fun PantallaPerfilPreview() {
    BienestarTheme {
        PantallaPerfil(
            onLogout = {},
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}