//features/Eventos/presentation/screens/CompraListScreen.kt
package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModel

@Composable
fun CompraListScreen(
    navController: NavController,
    viewModel: CompraViewModel = hiltViewModel()
) {
    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
        Text(
            text = "Eventos Disponibles",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VerdePrincipal)
            }
        } else if (eventos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay eventos disponibles", color = TextoSecundario)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(eventos) { evento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = evento.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdePrincipal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${evento.fecha}  ${evento.hora}", color = TextoSecundario, fontSize = 13.sp)
                            Text("Ubicación: ${evento.ubicacion}", color = TextoSecundario, fontSize = 13.sp)
                            Text("Stock: ${evento.stock}  |  Precio: $${evento.precio}", color = TextoSecundario, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    navController.navigate("verificacion/${evento.id}")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VerdePrincipal,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Comprar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoSecundario)
        ) {
            Text("Regresar")
        }
    }
}