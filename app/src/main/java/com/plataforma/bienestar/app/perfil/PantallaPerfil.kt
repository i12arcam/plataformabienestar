package com.plataforma.bienestar.app.perfil

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.ui.theme.BienestarTheme

@Composable
fun PantallaPerfil(
    onLogout: () -> Unit,
    onChangeName: (nuevoNombre: String) -> Unit,
    onChangePassword: (antiguaContrasena: String, nuevaContrasena: String) -> Unit,
    metodoAutenticacion: String,
    userName: String? = null,
    idUsuario: String,
    onNameUpdated: (String) -> Unit = {},
    tabViewModel: TabViewModel = viewModel()
) {
    // Estado para el nombre
    var currentUserName by remember { mutableStateOf(userName ?: "") }

    // Estados para controlar qué pantalla mostrar
    var showChangeNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // Estados para los formularios
    var newName by remember { mutableStateOf(userName ?: "") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Actualizar el estado local cuando cambia el prop
    LaunchedEffect(userName) {
        userName?.let { currentUserName = it }
    }

    BaseScreen(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                // Mostrar nombre de usuario si está disponible
                userName?.let { _ ->
                    Text(
                        text = "Usuario: $currentUserName",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón para cambiar nombre
                    Button(
                        onClick = { showChangeNameDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text("Cambiar nombre", textAlign = TextAlign.Center)
                    }

                    // Botón para cerrar sesión
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Cerrar sesión", textAlign = TextAlign.Center)
                    }

                    // Botón para cambiar contraseña
                    if(metodoAutenticacion == "Correo"){
                        Button(
                            onClick = { showChangePasswordDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        ) {
                            Text("Cambiar contraseña", textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    )

    // Diálogo para cambiar nombre
    if (showChangeNameDialog) {
        AlertDialog(
            onDismissRequest = { showChangeNameDialog = false },
            title = { Text("Cambiar nombre") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nuevo nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onChangeName(newName)
                        currentUserName = newName
                        onNameUpdated(newName) // Notificamos el cambio
                        showChangeNameDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showChangeNameDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para cambiar contraseña
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Cambiar contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Contraseña actual") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            onChangePassword(oldPassword, newPassword)
                            showChangePasswordDialog = false
                        }
                    },
                    enabled = newPassword.isNotEmpty() && newPassword == confirmPassword
                ) {
                    Text("Cambiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showChangePasswordDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// El Preview sigue exactamente igual
@Preview(showBackground = true)
@Composable
fun PantallaPerfilPreview() {
    BienestarTheme {
        PantallaPerfil(
            onLogout = {},
            onChangeName = {},
            onChangePassword = { _, _ -> },
            metodoAutenticacion = "Correo",
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1"
        )
    }
}