package com.plataforma.bienestar

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.plataforma.bienestar.acceso.registro.PantallaRegistro
import com.plataforma.bienestar.inicio.PantallaInicio
import com.plataforma.bienestar.inicio_sesion.PantallaInicioSesion

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {

    NavHost(navController = navHostController, startDestination = "inicio") {
        composable("inicio") {
            PantallaInicio(navigateToLogin = { navHostController.navigate("inicio_sesion") },
                navigateToSignUp = { navHostController.navigate("registro") })
        }
        composable("inicio_sesion") {
            PantallaInicioSesion(auth)
        }
        composable("registro") {
            PantallaRegistro(auth)
        }
        composable("home"){
            //HomeScreen()
        }
    }
}