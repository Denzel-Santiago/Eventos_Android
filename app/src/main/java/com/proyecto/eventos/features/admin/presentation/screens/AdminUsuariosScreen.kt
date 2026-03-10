// features/login/presentation/screens/AdminUsuariosScreen.kt
package com.proyecto.eventos.features.admin.presentation.screens

/*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.admin.presentation.viewmodel.AdminUsuariosViewModel

@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    viewModel: AdminUsuariosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()

    val NegroFondo = Color(0xFF0A0A0A)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroFondo)
            .statusBarsPadding()
    ) {
        // TopBar personalizado
        TopAppBar(
            title = {
                Text(
                    text = "Administrar Usuarios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextoSecundario
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF111111)
            )
        )

        // Contenido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (uiState.isLoading && usuarios.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VerdePrincipal
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Botón flotante de agregar
                    Button(
                        onClick = { viewModel.abrirDialogoNuevo() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Nuevo Usuario")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de usuarios
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(usuarios, key = { it.id }) { usuario ->
                            UsuarioAdminItem(
                                usuario = usuario,
                                onEditar = { viewModel.abrirDialogoEditar(usuario) },
                                onEliminar = { viewModel.eliminarUsuario(usuario.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de edición/creación
    if (uiState.mostrarDialog) {
        FormularioUsuarioDialog(
            viewModel = viewModel,
            uiState = uiState,
            onDismiss = { viewModel.cerrarDialogo() },
            onGuardar = { viewModel.guardarUsuario() }
        )
    }
}

@Composable
fun UsuarioAdminItem(
    usuario: UsuarioPreview,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111111)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = usuario.nombre,
                    color = TextoSecundario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = usuario.email,
                    color = TextoSecundario.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Surface(
                    color = if (usuario.rol == "ADMIN") VerdePrincipal.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = usuario.rol,
                        color = if (usuario.rol == "ADMIN") VerdePrincipal else TextoSecundario,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = VerdePrincipal
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioUsuarioDialog(
    viewModel: AdminUsuariosViewModel,
    uiState: AdminUsuariosViewModel.AdminUsuariosUiState,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit
) {
    val NegroContenedor = Color(0xFF111111)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    val roles = listOf("USER", "ADMIN")
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        containerColor = NegroContenedor,
        titleContentColor = VerdePrincipal,
        textContentColor = TextoSecundario,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (uiState.usuarioEditando == null) "Nuevo Usuario" else "Editar Usuario",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.dialogNombre,
                    onValueChange = { viewModel.onDialogNombreChange(it) },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = uiState.dialogError != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.dialogEmail,
                    onValueChange = { viewModel.onDialogEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = uiState.dialogError != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.dialogPassword,
                    onValueChange = { viewModel.onDialogPasswordChange(it) },
                    label = { Text(if (uiState.usuarioEditando == null) "Contraseña" else "Nueva contraseña (opcional)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = uiState.dialogError != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de rol
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = uiState.dialogRol,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Rol") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = darkTextFieldColors(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    viewModel.onDialogRolChange(role)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (uiState.dialogError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.dialogError!!,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextoSecundario
                    )
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    )
}*/