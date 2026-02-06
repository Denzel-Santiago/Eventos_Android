//features/Eventos/presentation/screens/InicioScreen.kt
package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.core.network.SessionManager
import com.proyecto.eventos.features.eventos.presentation.components.EventoItem
import com.proyecto.eventos.features.eventos.presentation.viewmodel.InicioViewModel
import com.proyecto.eventos.features.eventos.presentation.viewmodel.InicioViewModelFactory

@Composable
fun InicioScreen(
    navController: NavController,
    viewModel: InicioViewModel = viewModel(factory = InicioViewModelFactory())
) {

    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val estaAutenticado = SessionManager.estaAutenticado()
    val esAdmin = SessionManager.esAdmin()

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2C))
                .padding(16.dp)
        ) {
            Text(
                text = "Eventos App",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { viewModel.cargarEventos() }) {
                    Text("Inicio")
                }

                Button(onClick = {
                    if (estaAutenticado) {
                        navController.navigate("compra")
                    } else {
                        navController.navigate("login")
                    }
                }) {
                    Text("Comprar")
                }

                if (esAdmin) {
                    Button(onClick = { navController.navigate("admin_eventos") }) {
                        Text("Admin Eventos")
                    }
                    Button(onClick = { navController.navigate("admin_usuarios") }) {
                        Text("Admin Usuarios")
                    }
                }

                if (estaAutenticado) {
                    Button(
                        onClick = {
                            SessionManager.cerrarSesion()
                            navController.navigate("inicio") {
                                popUpTo("inicio") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Cerrar Sesión")
                    }
                } else {
                    Button(onClick = { navController.navigate("login") }) {
                        Text("Login")
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(eventos) { evento ->
                    EventoItem(
                        evento = evento,
                        onComprarClick = {
                            if (estaAutenticado) {
                                navController.navigate("compra")
                            } else {
                                navController.navigate("login")
                            }
                        }
                    )
                }
            }
        }
    }
}