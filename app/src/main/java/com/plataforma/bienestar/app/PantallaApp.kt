package com.plataforma.bienestar.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.plataforma.bienestar.app.emociones.PantallaEmociones
import com.plataforma.bienestar.app.home.PantallaHome

@Composable
fun PantallaApp (
    onLogout: () -> Unit,
    userName: String?,
    idUsuario: String,
    navController: NavHostController
    ) {
        val tabViewModel: TabViewModel = viewModel()

        when (tabViewModel.selectedTab.value) {
            "home" -> PantallaHome(idUsuario,navController)
            "programas" -> PantallaProgramas(userName, idUsuario)
            "emociones" -> PantallaEmociones(userName, idUsuario)
            "metas" -> PantallaMetas(userName, idUsuario)
            "perfil" -> PantallaPerfil(onLogout, userName, idUsuario)
        }
    }