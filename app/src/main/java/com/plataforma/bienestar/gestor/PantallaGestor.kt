package com.plataforma.bienestar.gestor

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.gestor.consejos.PantallaGestorConsejos
import com.plataforma.bienestar.gestor.programas.PantallaGestionProgramas
import com.plataforma.bienestar.gestor.recursos.PantallaGestionRecursos

@Composable
fun PantallaGestor(
    onLogout: () -> Unit,
    onChangePassword: (antiguaContrasena: String, nuevaContrasena: String) -> Unit,
    idUsuario: String
) {
    val tabViewModel: ViewModelGestor = viewModel()

    when (tabViewModel.selectedTab.value) {
        "consejos" -> PantallaGestorConsejos(
            idUsuario = idUsuario,
            tabViewModel = tabViewModel
        )
        "recursos" -> PantallaGestionRecursos(idUsuario)
        "programas" -> PantallaGestionProgramas(idUsuario)
        "perfil" -> PantallaGestionPerfil(
            onLogout = onLogout,
            onChangePassword = onChangePassword,
            tabViewModel = tabViewModel
        )
    }
}