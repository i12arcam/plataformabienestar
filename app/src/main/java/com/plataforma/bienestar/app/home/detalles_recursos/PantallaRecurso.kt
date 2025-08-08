package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plataforma.bienestar.R
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.GrayBlue
import com.plataforma.bienestar.ui.theme.LightPurple

@Composable
fun PantallaRecurso(
    usuarioId: String,
    recursoId: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel()
) {
    // Estados para manejar la carga del recurso
    var recurso by remember { mutableStateOf<Recurso?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar el recurso cuando se abre la pantalla o cambia el ID
    LaunchedEffect(recursoId) {
        isLoading = true
        try {
            recurso = ApiClient.apiService.getRecurso(recursoId)
        } catch (e: Exception) {
            error = "Error al cargar el recurso: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp))
                } else if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (recurso != null) {
                    // Rectángulo superior (Título del recurso)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GrayBlue)
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = "",
                                tint = DarkGreen,
                                modifier = Modifier
                                    .clickable { navController.popBackStack() }
                                    .padding(vertical = 20.dp, horizontal = 5.dp)
                                    .size(24.dp)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = recurso?.titulo ?: "",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )

                                recurso?.autor?.let { autor ->
                                    Text(
                                        text = "Por $autor",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                    }

                    // Contenido del recurso
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightPurple)
                            .padding(16.dp)
                    ) {
                        when (recurso?.tipo) {
                            "articulo" -> ArticuloContenido(recurso!!)
                            "video" -> VideoContenido(recurso!!)
                            "actividad" -> ActividadContenido(usuarioId, recurso!!, navController)
                        }
                    }
                }
            }
        }
    )
}