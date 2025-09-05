package com.plataforma.bienestar.gestor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plataforma.bienestar.ui.theme.BienestarTheme

@Composable
fun PantallaGestionPerfil(
    onLogout: () -> Unit,
    onChangePassword: (antiguaContrasena: String, nuevaContrasena: String) -> Unit,
    tabViewModel: ViewModelGestor = viewModel()
) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    BaseScreenGestor(
        selectedTab = tabViewModel.selectedTab.value,
        onTabSelected = { tab ->
            tabViewModel.selectTab(tab)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Mostrar "Perfil de Administrador" en lugar del nombre de usuario
                Text(
                    text = "Perfil de Administrador",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // SECCIÓN OPCIONES DE ADMINISTRADOR
                Text(
                    text = "Opciones de Administrador",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Botones para cerrar sesión y cambiar contraseña
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { showChangePasswordDialog = true },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    ) {
                        Text("Cambiar contraseña", textAlign = TextAlign.Center)
                    }

                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Cerrar sesión", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    )

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

                    // Mensaje de validación
                    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                            onChangePassword(oldPassword, newPassword)
                            // Limpiar campos después de cambiar
                            oldPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                            showChangePasswordDialog = false
                        }
                    },
                    enabled = newPassword.isNotEmpty() && newPassword == confirmPassword && oldPassword.isNotEmpty()
                ) {
                    Text("Cambiar contraseña")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangePasswordDialog = false
                        // Limpiar campos al cancelar
                        oldPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaGestionPerfilPreview() {
    BienestarTheme {
        PantallaGestionPerfil(
            onLogout = {},
            onChangePassword = { _, _ -> }
        )
    }
}