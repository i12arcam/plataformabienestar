package com.plataforma.bienestar

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.plataforma.bienestar.acceso.registro.PantallaRegistro
import com.plataforma.bienestar.acceso.registro.auth.GoogleAuthManager
import com.plataforma.bienestar.home.PantallaHome
import com.plataforma.bienestar.acceso.registro.inicio.PantallaInicio
import com.plataforma.bienestar.acceso.registro.inicio_sesion.PantallaInicioSesion

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    googleAuthManager: GoogleAuthManager,
    startDestination: String = "inicio"
) {
    NavHost(navController = navHostController, startDestination) {
        composable("inicio") {
            PantallaInicio(
                navigateToLogin = { navHostController.navigate("inicio_sesion") },
                navigateToSignUp = { navHostController.navigate("registro") },
                onGoogleSignInClick = {
                    googleAuthManager.launchSignIn(
                        onSuccess = { user ->
                            Log.i("GoogleSignIn", "Inicio de sesión exitoso: ${user.displayName}")
                            navHostController.navigate("home") {
                                popUpTo("inicio") { inclusive = true }
                            }
                        },
                        onFailure = { error: String ->
                            Log.e("GoogleSignIn", error)
                            // Puedes mostrar un mensaje de error al usuario aquí
                        }
                    )
                }
            )
        }
        composable("inicio_sesion") {
            PantallaInicioSesion(
                auth = auth,
                navController = navHostController
            )
        }

        composable("registro") {
            PantallaRegistro(
                auth = auth,
                navController = navHostController
            )
        }

        composable("home") {
            val currentUser = auth.currentUser
            PantallaHome(
                onLogout = {
                    googleAuthManager.signOut {
                        navHostController.navigate("inicio") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                userName = currentUser?.displayName
            )
        }
    }
}