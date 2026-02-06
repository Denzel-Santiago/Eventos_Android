package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModel
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModelFactory

@Composable
fun CompraScreen(
    navController: NavController,
    viewModel: CompraViewModel = viewModel(factory = CompraViewModelFactory())
) {

    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Eventos Disponibles",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(eventos) { evento ->
                EventoCompraItem(
                    evento = evento,
                    onComprarClick = {
                        viewModel.comprarBoleto(evento)
                    }
                )
            }
        }

        mensaje?.let {
            AlertDialog(
                onDismissRequest = { viewModel.limpiarMensaje() },
                confirmButton = {
                    Button(onClick = { viewModel.limpiarMensaje() }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Compra exitosa") },
                text = { Text(it) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar al inicio")
        }
    }
}

@Composable
fun EventoCompraItem(
    evento: EventoEntidad,
    onComprarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = evento.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Ubicación: ${evento.ubicacion}")
            Text("Fecha: ${evento.fecha}")
            Text("Boletos disponibles: ${evento.boletosDisponibles}")
            Text("Precio: $${evento.precio}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onComprarClick) {
                Text("Comprar")
            }
        }
    }
}