package com.proyecto.eventos.features.admin.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import com.proyecto.eventos.features.admin.presentation.viewmodel.AdminUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    viewModel: AdminUsuariosViewModel = hiltViewModel()
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val mensajeError by viewModel.mensajeError.collectAsStateWithLifecycle()
    val usuarioAEliminar by viewModel.usuarioAEliminar.collectAsStateWithLifecycle()

    val NegroFondo = Color(0xFF0A0A0A)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    // Diálogo de confirmación de eliminación
    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelarEliminar() },
            containerColor = Color(0xFF111111),
            titleContentColor = VerdePrincipal,
            textContentColor = TextoSecundario,
            title = { Text("Eliminar usuario", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Seguro que deseas eliminar a \"${usuario.nombre}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarEliminar() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.cancelarEliminar() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoSecundario)
                ) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestionar Usuarios",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdePrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = TextoSecundario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF111111))
            )
        },
        containerColor = NegroFondo
    ) { paddingValues ->

        if (isLoading && usuarios.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VerdePrincipal)
            }
        } else if (usuarios.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.People, contentDescription = null,
                        tint = VerdePrincipal, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay usuarios registrados", color = TextoSecundario, fontSize = 16.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Contador
                Text(
                    text = "${usuarios.size} usuario${if (usuarios.size != 1) "s" else ""} registrado${if (usuarios.size != 1) "s" else ""}",
                    color = TextoSecundario.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(usuarios, key = { it.uid }) { usuario ->
                        UsuarioAdminCard(
                            usuario = usuario,
                            verdePrincipal = VerdePrincipal,
                            textoSecundario = TextoSecundario,
                            onEliminar = { viewModel.pedirConfirmacionEliminar(usuario) }
                        )
                    }
                }
            }
        }

        // Snackbar de error
        mensajeError?.let { error ->
            LaunchedEffect(error) {
                viewModel.limpiarError()
            }
        }
    }
}

@Composable
fun UsuarioAdminCard(
    usuario: UsuarioAdminEntidad,
    verdePrincipal: Color,
    textoSecundario: Color,
    onEliminar: () -> Unit
) {
    val esAdmin = usuario.rol.lowercase() == "admin"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar inicial
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (esAdmin) verdePrincipal.copy(alpha = 0.2f)
                                else Color.Gray.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                    color = if (esAdmin) verdePrincipal else textoSecundario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombre,
                    color = textoSecundario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (usuario.email.isNotBlank()) {
                    Text(
                        text = usuario.email,
                        color = textoSecundario.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (esAdmin) verdePrincipal.copy(alpha = 0.15f)
                            else Color.Gray.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (esAdmin) "ADMIN" else "USUARIO",
                        color = if (esAdmin) verdePrincipal else textoSecundario.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Solo eliminar si no es admin
            if (!esAdmin) {
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Candado para el admin — no se puede eliminar
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Protegido",
                    tint = verdePrincipal.copy(alpha = 0.4f),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
