package com.plataforma.bienestar.acceso.registro.registro

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.plataforma.bienestar.R
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.Usuario
import com.plataforma.bienestar.ui.theme.BackgroundGreen
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.MainGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaRegistro(auth: FirebaseAuth, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGreen)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(){
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "",
                tint = White,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(vertical = 24.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Text("Nombre", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = BackgroundGreen
            )
        )
        Spacer(Modifier.height(48.dp))
        Text("Correo", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = BackgroundGreen
            )
        )
        Spacer(Modifier.height(48.dp))
        Text("Contraseña", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        TextField(
            value = password, onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = BackgroundGreen
            )
        )
        Spacer(Modifier.height(48.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen
            ),
            onClick = {
                // 1. Registro en Firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser

                            // Actualizar el perfil del usuario con el nombre
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)  // Aquí asignas el nombre
                                .build()

                            firebaseUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Log.d("Registro", "Nombre actualizado en Firebase Auth")

                                        // Ahora puedes proceder con el registro en tu backend
                                        val userId = firebaseUser.uid
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                ApiClient.apiService.createUser(
                                                    Usuario(
                                                        firebaseUID = userId,
                                                        nombre = name,
                                                        email = email
                                                    )
                                                )
                                                withContext(Dispatchers.Main) {
                                                    navController.navigate("home") {
                                                        popUpTo("registro") { inclusive = true }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e("Registro", "Error en backend: ${e.message}")
                                            }
                                        }
                                    } else {
                                        Log.e("Registro", "Error al actualizar nombre: ${updateTask.exception?.message}")
                                    }
                                }
                        } else {
                            Log.e("Registro", "Error en Firebase: ${task.exception?.message}")
                        }
                    }
            }
        ) {
            Text(text = "Registrarse")
        }
    }
}