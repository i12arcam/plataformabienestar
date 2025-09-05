package com.plataforma.bienestar.app.perfil

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.plataforma.bienestar.app.BaseScreen
import com.plataforma.bienestar.app.TabViewModel
import com.plataforma.bienestar.data.api.ApiClient
import com.plataforma.bienestar.data.api.model.EmocionGrafica
import com.plataforma.bienestar.data.api.model.Logro
import com.plataforma.bienestar.ui.theme.BienestarTheme
import java.util.Locale
import com.plataforma.bienestar.data.api.model.UsuarioProgreso

@Composable
fun PantallaPerfil(
    onLogout: () -> Unit,
    onChangeName: (nuevoNombre: String) -> Unit,
    onChangePassword: (antiguaContrasena: String, nuevaContrasena: String) -> Unit,
    metodoAutenticacion: String,
    userName: String? = null,
    idUsuario: String,
    navController: NavController,
    onNameUpdated: (String) -> Unit = {},
    tabViewModel: TabViewModel = viewModel()
) {
    // Estados para la gráfica
    val emociones = remember { mutableStateListOf<EmocionGrafica>() }
    val isLoading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }

    // Cargar emociones al iniciar
    LaunchedEffect(idUsuario) {
        isLoading.value = true
        try {
            val response = ApiClient.apiService.getEmocionesHistorial(idUsuario)
            emociones.clear()
            emociones.addAll(response.emociones)
            Log.d("Emociones","emociones: $emociones")
            isLoading.value = false
        } catch (e: Exception) {
            error.value = "Error al cargar emociones: ${e.message}"
            isLoading.value = false
            Log.e("Emociones", error.value!!)
        }
    }

    // Estados para el progreso del usuario
    val progreso = remember { mutableStateOf<UsuarioProgreso?>(null) }
    val isLoadingProgreso = remember { mutableStateOf(false) }
    val errorProgreso = remember { mutableStateOf<String?>(null) }

    // Cargar progreso al iniciar
    LaunchedEffect(idUsuario) {
        isLoadingProgreso.value = true
        try {
            val response = ApiClient.apiService.obtenerXp(idUsuario)
            progreso.value = response
            isLoadingProgreso.value = false
        } catch (e: Exception) {
            errorProgreso.value = "Error al cargar progreso: ${e.message}"
            isLoadingProgreso.value = false
            Log.e("Progreso", errorProgreso.value!!)
        }
    }

    // Estados para logros
    val logros = remember { mutableStateListOf<Logro>() }
    val isLoadingLogros = remember { mutableStateOf(false) }
    val errorLogros = remember { mutableStateOf<String?>(null) }

    // Cargar logros al iniciar
    LaunchedEffect(idUsuario) {
        isLoadingLogros.value = true
        try {
            val response = ApiClient.apiService.obtenerLogrosUsuario(idUsuario)
            logros.clear()
            logros.addAll(response)
            isLoadingLogros.value = false
        } catch (e: Exception) {
            errorLogros.value = "Error al cargar logros: ${e.message}"
            isLoadingLogros.value = false
            Log.e("Logros", errorLogros.value!!)
        }
    }

    // Estados existentes
    var currentUserName by remember { mutableStateOf(userName ?: "") }

    var showChangeNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(userName ?: "") }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var historialActivo by remember { mutableStateOf<String?>(null) }
    var estadoItem by remember { mutableStateOf("completado") }

    // Definir los estados disponibles según el tipo de historial
    val estadosDisponibles = remember(historialActivo) {
        when (historialActivo) {
            "recursos" -> listOf("en_progreso","visto", "completado")
            "programas" -> listOf("en_progreso","completado")
            else -> emptyList()
        }
    }

    // Actualizar el estado local cuando cambia el prop
    LaunchedEffect(userName) {
        userName?.let { currentUserName = it }
    }

    // Resetear el estadoItem cuando cambia el historialActivo
    LaunchedEffect(historialActivo) {
        if (historialActivo != null) {
            // Establecer el primer estado disponible como predeterminado
            estadoItem = estadosDisponibles.firstOrNull() ?: "completado"
        }
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Mostrar nombre de usuario
                userName?.let { _ ->
                    Text(
                        text = currentUserName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // SECCIÓN GRÁFICAS
                Text(
                    text = "Estadísticas emocionales de este mes:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 32.dp))
                } else if (error.value != null) {
                    Text(
                        text = error.value!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (emociones.isEmpty()) {
                    Text(
                        text = "No hay datos emocionales para mostrar",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Mostrar gráfica
                    GraficaEmociones(
                        emociones = emociones,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // SECCIÓN ESTADÍSTICAS PROGRESO

                Text(
                    text = "Estadísticas de Progreso:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                )

                if (isLoadingProgreso.value) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp))
                } else if (errorProgreso.value != null) {
                    Text(
                        text = errorProgreso.value!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (progreso.value == null) {
                    Text(
                        text = "No hay datos de progreso",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Mostrar estadísticas de progreso - Versión mejorada
                    val progresoData = progreso.value!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Encabezado
                            Text(
                                text = "Mi Progreso",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Primera fila: Nivel y Racha
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Nivel actual
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Nivel", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${progresoData.nivel}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Separador
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(Color.LightGray)
                                )

                                // Racha actual
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Racha", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${progresoData.streak} días",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = if (progresoData.streak > 0) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }

                                // Separador
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(Color.LightGray)
                                )

                                // XP Total
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("XP Total", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${progresoData.xpTotal}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Barra de progreso
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Información de progreso
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Progreso al siguiente nivel:",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "${progresoData.xpNivelActual}/${progresoData.xpSiguienteNivel} XP",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Barra de progreso
                                LinearProgressIndicator(
                                    progress = { progresoData.xpNivelActual.toFloat() / progresoData.xpSiguienteNivel.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }

                // SECCIÓN LOGROS
                Text(
                    text = "Logros",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                )

                if (isLoadingLogros.value) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp))
                } else if (errorLogros.value != null) {
                    Text(
                        text = errorLogros.value!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (logros.isEmpty()) {
                    Text(
                        text = "Aún no has desbloqueado logros",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Mostrar logros - QUITAR el modifier con altura fija
                    GridLogros(
                        logros = logros,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // SECCIÓN HISTORIALES

                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Selector de tipo de historial
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = historialActivo == "recursos",
                        onClick = {
                            historialActivo = if (historialActivo == "recursos") null else "recursos"
                        },
                        label = { Text("Recursos") }
                    )
                    FilterChip(
                        selected = historialActivo == "programas",
                        onClick = {
                            historialActivo = if (historialActivo == "programas") null else "programas"
                        },
                        label = { Text("Programas") }
                    )
                }

                // Selector de estado (solo si hay historial activo)
                if (historialActivo != null && estadosDisponibles.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        estadosDisponibles.forEach { estado ->
                            FilterChip(
                                selected = estadoItem == estado,
                                onClick = { estadoItem = estado },
                                label = {
                                    Text(
                                        estado.replace("_", " ")
                                            .replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                                else it.toString()
                                            }
                                    )
                                }
                            )
                        }
                    }
                }

                // Mostrar el historial seleccionado
                when (historialActivo) {
                    "recursos" -> HistorialRecursos(
                        idUsuario = idUsuario,
                        estado = estadoItem,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .padding(top = 8.dp)
                    )
                    "programas" -> HistorialProgramas(
                        idUsuario = idUsuario,
                        estado = estadoItem,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .padding(top = 8.dp)
                    )
                }

                Text(
                    text = "Opciones Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Botones existentes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showChangeNameDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text("Cambiar nombre", textAlign = TextAlign.Center)
                    }

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

                    if(metodoAutenticacion == "Correo") {
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
                        onNameUpdated(newName)
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

@Preview(showBackground = true)
@Composable
fun PantallaPerfilPreview() {
    BienestarTheme {
        val navController = rememberNavController()
        PantallaPerfil(
            onLogout = {},
            onChangeName = {},
            onChangePassword = { _, _ -> },
            metodoAutenticacion = "Correo",
            userName = "Usuario Ejemplo",
            idUsuario = "UHbnffsmeDQHuGOY4dig8sW9yRy1",
            navController = navController
        )
    }
}