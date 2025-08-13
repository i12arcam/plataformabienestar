package com.plataforma.bienestar

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.plataforma.bienestar.acceso.registro.registro.PantallaRegistro
import com.plataforma.bienestar.acceso.registro.auth.GoogleAuthManager
import com.plataforma.bienestar.acceso.registro.inicio.PantallaInicio
import com.plataforma.bienestar.acceso.registro.inicio_sesion.PantallaInicioSesion
import com.plataforma.bienestar.app.PantallaApp
import com.plataforma.bienestar.app.home.PantallaBusqueda
import com.plataforma.bienestar.app.home.detalles_recursos.PantallaRecurso
import com.google.firebase.auth.UserProfileChangeRequest

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
            var userName by remember { mutableStateOf(currentUser?.displayName ?: "") }

            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navHostController.navigate("inicio") {
                        popUpTo("app") { inclusive = true }
                    }
                }
                return@composable
            }

            LaunchedEffect(currentUser.displayName) {
                currentUser.displayName?.let { name ->
                    userName = name
                }
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
                onChangeName = { nuevoNombre ->
                    currentUser.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(nuevoNombre)
                            .build()
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ProfileUpdate", "Nombre actualizado correctamente")
                            // No necesitamos actualizar userName aquí, el LaunchedEffect lo hará
                        } else {
                            Log.e("ProfileUpdate", "Error al actualizar nombre", task.exception)
                        }
                    }
                },
                onChangePassword = { antiguaContrasena, nuevaContrasena ->
                    val credential = EmailAuthProvider.getCredential(currentUser.email ?: "", antiguaContrasena)
                    currentUser.reauthenticate(credential)
                        .addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                currentUser.updatePassword(nuevaContrasena)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            Log.d("PasswordUpdate", "Contraseña actualizada correctamente")
                                        } else {
                                            Log.e("PasswordUpdate", "Error al actualizar contraseña", updateTask.exception)
                                        }
                                    }
                            } else {
                                Log.e("Reauthentication", "Error en reautenticación", reauthTask.exception)
                            }
                        }
                },
                metodoAutenticacion = when {
                    currentUser.providerData.any { it.providerId == "google.com" } -> "Google"
                    currentUser.providerData.any { it.providerId == "password" } -> "Correo"
                    else -> "Desconocido"
                },
                userName = userName,
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
            route = "recurso_detalle/{recursoId}/{usuarioId}",
            arguments = listOf(
                navArgument("recursoId") { type = NavType.StringType },
                navArgument("usuarioId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""

            PantallaRecurso(
                recursoId = recursoId,
                usuarioId = usuarioId,
                navController = navHostController
            )
        }
    }
}