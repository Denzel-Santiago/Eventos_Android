//features/login/presentation/screens/AdminUsuariosScreen.kt
package com.proyecto.eventos.features.login.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.login.presentation.components.UsuarioAdminItem
import com.proyecto.eventos.features.login.presentation.viewmodel.AdminUsuariosViewModel
import com.proyecto.eventos.features.login.presentation.viewmodel.AdminUsuariosViewModelFactory

@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    viewModel: AdminUsuariosViewModel = viewModel(factory = AdminUsuariosViewModelFactory())
) {

    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Administrar Usuarios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(usuarios) { usuario ->
                UsuarioAdminItem(
                    usuario = usuario,
                    onEditar = { viewModel.editarUsuario(usuario) },
                    onEliminar = { viewModel.eliminarUsuario(usuario.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }


    if (viewModel.mostrarFormulario) {
        FormularioUsuarioDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.cerrarFormulario() },
            onGuardar = { viewModel.guardarUsuario() }
        )
    }
}

@Composable
fun FormularioUsuarioDialog(
    viewModel: AdminUsuariosViewModel,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}