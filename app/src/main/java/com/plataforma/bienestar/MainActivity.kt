package com.plataforma.bienestar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plataforma.bienestar.ui.theme.BienestarTheme
import com.plataforma.bienestar.auth.GoogleAuthManager

class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuthManager: GoogleAuthManager

    // ActivityResultLauncher para manejar el resultado de inicio de sesión de Google
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        googleAuthManager = GoogleAuthManager(this)

        val user = FirebaseAuth.getInstance().currentUser
        Log.d("FirebaseUser", "Usuario autenticado: ${user?.displayName}, Email: ${user?.email}")


        // Registrar el launcher para manejar el resultado de la actividad de inicio de sesión de Google
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                googleAuthManager.handleSignInResult(
                    requestCode = result.resultCode,
                    data = result.data,
                    onSuccess = { message ->
                        Log.i("GoogleSignIn", message)
                        navHostController.navigate("home") // Redirigir a "home" en caso de éxito
                    },
                    onFailure = { error ->
                        Log.e("GoogleSignIn", error)
                    }
                )
            }
        }

        setContent {
            navHostController = rememberNavController()
            BienestarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationWrapper(navHostController, auth)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario ya está logado, redirigimos a la pantalla de inicio
            Log.i("MainActivity", "Usuario logueado: ${currentUser.displayName}")
            navHostController.navigate("home")  // Redirigir a la pantalla principal
        } else {
            // Si el usuario no está logado, lanzamos el inicio de sesión con Google
            googleAuthManager.signInWithGoogle(
                onSuccess = { message ->
                    Log.i("GoogleSignIn", message)
                    navHostController.navigate("home") // Redirigir al "home"
                },
                onFailure = { error ->
                    Log.e("GoogleSignIn", error)
                }
            )
        }
    }
}
