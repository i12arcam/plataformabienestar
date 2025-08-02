package com.plataforma.bienestar

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.plataforma.bienestar.acceso.registro.registro.PantallaRegistro
import com.plataforma.bienestar.acceso.registro.auth.GoogleAuthManager
import com.plataforma.bienestar.acceso.registro.inicio.PantallaInicio
import com.plataforma.bienestar.acceso.registro.inicio_sesion.PantallaInicioSesion
import com.plataforma.bienestar.app.PantallaApp
import com.plataforma.bienestar.app.home.PantallaBusqueda
import com.plataforma.bienestar.app.home.detalles_recursos.PantallaRecurso

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
                            navHostController.navigate("app") {
                                popUpTo("inicio") { inclusive = true }
                            }
                        },
                        onFailure = { error: String ->
                            Log.e("GoogleSignIn", error)
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

        composable("app") {
            val currentUser = auth.currentUser

            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navHostController.navigate("inicio") {
                        popUpTo("app") { inclusive = true }
                    }
                }
                return@composable
            }

            PantallaApp(
                onLogout = {
                    auth.signOut()
                    googleAuthManager.signOut {
                        navHostController.navigate("inicio") {
                            popUpTo("app") { inclusive = true }
                        }
                    }
                },
                userName = currentUser.displayName,
                idUsuario = currentUser.uid,
                navController = navHostController
            )
        }

        composable(
            route = "busqueda_recursos/{parametro}",
            arguments = listOf(
                navArgument("parametro") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val parametro = backStackEntry.arguments?.getString("parametro") ?: ""
            PantallaBusqueda(
                parametro = parametro,
                navController = navHostController
            )
        }

        composable(
            route = "recurso_detalle/{recursoId}",
            arguments = listOf(
                navArgument("recursoId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            PantallaRecurso(
                recursoId = recursoId,
                navController = navHostController
            )
        }
    }
}