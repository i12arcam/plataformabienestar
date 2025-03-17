package com.plataforma.bienestar

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.plataforma.bienestar.acceso.registro.PantallaRegistro
import com.plataforma.bienestar.inicio.PantallaInicio
import com.plataforma.bienestar.inicio_sesion.PantallaInicioSesion
import com.plataforma.bienestar.ui.theme.BienestarTheme

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {

    NavHost(navController = navHostController, startDestination = "home") {
        composable("inicio") {
            PantallaInicio()
        }
        composable("inicio_sesion") {
            PantallaInicioSesion()
        }
        composable("registro") {
            PantallaRegistro()
        }
        composable("home"){
            //HomeScreen()
        }
    }
}