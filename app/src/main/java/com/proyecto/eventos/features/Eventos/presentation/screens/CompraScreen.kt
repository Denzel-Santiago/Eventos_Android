package com.proyecto.eventos.features.Eventos.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.Eventos.presentation.components.EventoCompraItem
import com.proyecto.eventos.features.Eventos.presentation.viewmodel.CompraViewModel

@Composable
fun CompraScreen(
    navController: NavController,
    viewModel: CompraViewModel = viewModel()
) {
    val eventos by viewModel.eventos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER SIMPLE
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
                        viewModel.comprarBoletos(evento)
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
