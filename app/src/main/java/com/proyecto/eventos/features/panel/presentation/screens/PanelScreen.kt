package com.proyecto.eventos.features.panel.presentation.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.panel.presentation.viewmodel.PanelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelScreen(
    navController: NavController,
    viewModel: PanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accion by viewModel.accion.collectAsStateWithLifecycle()

    LaunchedEffect(accion) {
        when (accion) {
            is PanelViewModel.PanelAccion.Logout -> {
                navController.navigate("login") {
                    popUpTo("panel") { inclusive = true }
                }
                viewModel.limpiarAccion()
            }
            null -> {}
        }
    }

    val negroFondo = Color(0xFF0A0A0A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFFE5E7EB)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sweeper Tickets",
                        color = verdePrincipal,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshUserData() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = textoSecundario
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = verdePrincipal,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = textoSecundario
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111111)
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.userName.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(negroFondo)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = verdePrincipal, strokeWidth = 3.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(negroFondo)
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = uiState.welcomeMessage,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textoSecundario
                        )
                        if (uiState.userEmail.isNotEmpty()) {
                            Text(
                                text = uiState.userEmail,
                                fontSize = 14.sp,
                                color = textoSecundario.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (uiState.isAdmin) {
                            Surface(
                                color = verdePrincipal.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "ADMINISTRADOR",
                                    color = verdePrincipal,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OpcionMenuPrincipal(
                            titulo = "Comprar Boletos",
                            descripcion = "Explora eventos disponibles y compra tus boletos",
                            icono = Icons.Default.ShoppingCart,
                            colorIcono = verdePrincipal,
                            onClick = { navController.navigate("eventos") }
                        )
                    }
                    item {
                        OpcionMenuPrincipal(
                            titulo = "Mis Favoritos",
                            descripcion = "Eventos que has guardado para después",
                            icono = Icons.Default.Favorite,
                            colorIcono = Color.Red,
                            onClick = { navController.navigate("favoritos") }
                        )
                    }
                    item {
                        OpcionMenuPrincipal(
                            titulo = "Historial de Compras",
                            descripcion = "Tus compras anteriores",
                            icono = Icons.Default.History,
                            colorIcono = Color(0xFFFFA500),
                            onClick = { navController.navigate("historial") }
                        )
                    }

                    if (uiState.isAdmin) {
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                        }
                        item {
                            Text(
                                text = "Administración",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = verdePrincipal,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            OpcionMenuPrincipal(
                                titulo = "Gestionar Eventos",
                                descripcion = "Crear, editar o eliminar eventos",
                                icono = Icons.Default.Event,
                                colorIcono = verdePrincipal,
                                onClick = { navController.navigate("admin/eventos") }
                            )
                        }
                        item {
                            OpcionMenuPrincipal(
                                titulo = "Gestionar Usuarios",
                                descripcion = "Administrar cuentas de usuario",
                                icono = Icons.Default.People,
                                colorIcono = verdePrincipal,
                                onClick = { navController.navigate("admin/usuarios") }
                            )
                        }
                    }
                }

                Text(
                    text = "Versión 2.0.0",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OpcionMenuPrincipal(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = colorIcono.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = descripcion,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}
