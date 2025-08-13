// PantallaApp.kt - Se mantiene exactamente igual que tu versión original
package com.plataforma.bienestar.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.plataforma.bienestar.app.emociones.PantallaEmociones
import com.plataforma.bienestar.app.home.PantallaHome
import com.plataforma.bienestar.app.metas.PantallaMetas
import com.plataforma.bienestar.app.perfil.PantallaPerfil

@Composable
fun PantallaApp(
    onLogout: () -> Unit,
    onChangeName: (nuevoNombre: String) -> Unit,
    onChangePassword: (antiguaContrasena: String, nuevaContrasena: String) -> Unit,
    metodoAutenticacion: String,
    userName: String?,
    idUsuario: String,
    navController: NavHostController
) {
    val tabViewModel: TabViewModel = viewModel()
    var currentUserName by remember { mutableStateOf(userName ?: "") }

    // Actualizamos el estado cuando cambia el prop
    LaunchedEffect(userName) {
        userName?.let { currentUserName = it }
    }

    when (tabViewModel.selectedTab.value) {
        "home" -> PantallaHome(idUsuario, navController)
        "programas" -> PantallaProgramas(currentUserName, idUsuario)
        "emociones" -> PantallaEmociones(idUsuario)
        "metas" -> PantallaMetas(idUsuario)
        "perfil" -> PantallaPerfil(
            onLogout = onLogout,
            onChangeName = { newName ->
                onChangeName(newName)
                currentUserName = newName
            },
            onChangePassword = onChangePassword,
            metodoAutenticacion = metodoAutenticacion,
            userName = currentUserName,
            idUsuario = idUsuario,
            onNameUpdated = { newName -> currentUserName = newName }
        )
    }
}