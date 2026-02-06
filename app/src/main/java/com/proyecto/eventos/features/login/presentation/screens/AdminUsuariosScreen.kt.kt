//features/login/presentation/screens/AdminUsuariosScreen.kt
package com.proyecto.eventos.features.login.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val NegroFondo = Color(0xFF0A0A0A)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroFondo)
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        // 🔹 TÍTULO
        Text(
            text = "Administrar Usuarios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 LISTA (ocupa todo el espacio)
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(usuarios) { usuario ->
                UsuarioAdminItem(
                    usuario = usuario,
                    onEditar = { viewModel.editarUsuario(usuario) },
                    onEliminar = { viewModel.eliminarUsuario(usuario.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 ACCIÓN GLOBAL
        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextoSecundario
            )
        ) {
            Text("Regresar al inicio")
        }
    }

    // 🔹 DIALOG
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
    val NegroContenedor = Color(0xFF111111)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    AlertDialog(
        containerColor = NegroContenedor,
        titleContentColor = VerdePrincipal,
        textContentColor = TextoSecundario,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Usuario",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )
            }
        },

        // 🔹 BOTONES BIEN POSICIONADOS
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Guardar")
                }
            }
        }
    )
}
