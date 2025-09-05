package com.plataforma.bienestar

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
    private var alreadyHandledNavigation = false

    // ✅ Registrar el contrato para solicitar permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones concedido")
        } else {
            Log.w("MainActivity", "⚠Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleAuthManager = GoogleAuthManager(this, lifecycleScope)

        // Solicitar permiso de notificaciones (solo Android 13+)
        requestNotificationPermission()

        setContent {
            BienestarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val controller = rememberNavController()
                    navHostController = controller

                    // Determinar el destino inicial basado en el usuario y su email
                    val startDestination = determineStartDestination()

                    if (!alreadyHandledNavigation) {
                        NavigationWrapper(
                            navHostController = controller,
                            auth = auth,
                            googleAuthManager = googleAuthManager,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }

    private fun determineStartDestination(): String {
        return auth.currentUser?.let { user ->
            if (user.email.equals("mindauragestor@gmail.com", ignoreCase = true)) {
                "gestor"
            } else {
                "app"
            }
        } ?: "inicio" // Si no hay usuario, ir a inicio
    }

    // ✅ Función para solicitar permiso de notificaciones
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Permiso de notificaciones ya concedido")
                }
                else -> {
                    // Mostrar diálogo explicativo antes de pedir permiso (opcional pero recomendado)
                    Log.d("MainActivity", "Solicitando permiso de notificaciones...")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d("MainActivity", "Android <13, no necesita permiso explícito")
        }
    }
}