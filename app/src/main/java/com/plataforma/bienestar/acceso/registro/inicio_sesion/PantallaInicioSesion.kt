package com.plataforma.bienestar.acceso.registro.inicio_sesion

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.plataforma.bienestar.R
import com.plataforma.bienestar.ui.theme.BackgroundGreen
import com.plataforma.bienestar.ui.theme.DarkGreen
import com.plataforma.bienestar.ui.theme.MainGreen
import com.plataforma.bienestar.util.GestorXP

@Composable
fun PantallaInicioSesion(
    auth: FirebaseAuth,
    navController: NavHostController
)
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGreen)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row{
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "",
                tint = White,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
                    .padding(vertical = 24.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
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
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Log.d("Admin", email)
                    if(email.equals("mindauragestor@gmail.com", ignoreCase = true)) {
                        navController.navigate("gestor") {
                            popUpTo("inicio_sesion") { inclusive = true }
                        }
                    } else {
                        GestorXP.registrarAccionYOtorgarXP(
                            usuarioId = auth.uid!!,
                            evento = "iniciar_sesion",
                            scope = scope
                        )
                        navController.navigate("app") {
                            popUpTo("inicio_sesion") { inclusive = true }
                        }
                    }
                    Log.i("aris", "LOGIN OK")
                }else{
                    //Error
                    Log.i("aris", "LOGIN KO")
                }
            }
        }) {
            Text(text = "Iniciar Sesión")
        }
    }
}

