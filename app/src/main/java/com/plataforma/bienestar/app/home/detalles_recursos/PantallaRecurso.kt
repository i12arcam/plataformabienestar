package com.plataforma.bienestar.app.home.detalles_recursos

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Recurso

@Composable
fun PantallaRecurso(
    usuarioId: String,
    recursoId: String,
    navController: NavController,
    tabViewModel: TabViewModel = viewModel()
) {
    // Estados para manejar la carga del recurso
    var recurso by remember { mutableStateOf<Recurso?>(null) }
    var estado by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Cargar el recurso cuando se abre la pantalla o cambia el ID
    LaunchedEffect(recursoId) {
        isLoading = true
        try {
            recurso = ApiClient.apiService.getRecurso(recursoId)

            try {
                estado = ApiClient.apiService.getEstadoRecurso(usuarioId, recurso!!.id)
            } catch (e: Exception) {
                Log.e("PantallaRecurso", "Error al obtener estado del recurso: ${e.message}")
                estado = null // No hay estado disponible
            }
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
        showNavigationBar = false,
        content = { _ ->
            DetallesRecurso(
                usuarioId = usuarioId,
                recurso = recurso,
                isLoading = isLoading,
                error = error,
                navController = navController,
                estaEnPrograma = false,
                onBackClick = { navController.popBackStack() },
                estado = estado
            )
        }
    )
}