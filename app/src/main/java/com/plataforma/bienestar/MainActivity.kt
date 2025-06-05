package com.plataforma.bienestar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plataforma.bienestar.acceso.registro.auth.GoogleAuthManager
import com.plataforma.bienestar.ui.theme.BienestarTheme

class MainActivity : ComponentActivity() {
    private var navHostController: NavHostController? = null
    private lateinit var googleAuthManager: GoogleAuthManager
    private val auth: FirebaseAuth = Firebase.auth
    private var alreadyHandledNavigation = false // Bandera para controlar la navegación

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleAuthManager = GoogleAuthManager(this, lifecycleScope)

        setContent {
            BienestarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val controller = rememberNavController()
                    navHostController = controller

                    if (!alreadyHandledNavigation) {
                        NavigationWrapper(
                            navHostController = controller,
                            auth = auth,
                            googleAuthManager = googleAuthManager,
                            startDestination = if (auth.currentUser != null) "home" else "inicio"
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!alreadyHandledNavigation) {
            navHostController?.let { controller ->
                auth.currentUser?.let { user ->
                    Log.i("MainActivity", "Usuario ya logueado: ${user.email}")
                    controller.navigate("home") {
                        popUpTo(0) // Limpia toda la pila
                    }
                    alreadyHandledNavigation = true
                }
            }
        }
    }
}