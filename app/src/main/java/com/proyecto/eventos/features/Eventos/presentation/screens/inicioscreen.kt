package com.proyecto.eventos.features.Eventos.presentation.screens

import android.os.Build.VERSION_CODES_FULL.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.Eventos.presentation.components.EventoItem
import com.proyecto.eventos.features.Eventos.presentation.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    navController: NavController,
    viewModel: InicioViewModel = viewModel()
) {
    val eventos by viewModel.eventos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            Text(
                text = "Sweeper Tickets",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { }) {
                    Text("Inicio")
                }
                Button(onClick = {
                    navController.navigate("compra")
                }) {
                    Text("Compra")
                }
                Button(onClick = {
                    navController.navigate("login")
                }) {
                    Text("Login")
                }
            }
        }

        // LISTA DE EVENTOS
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(eventos) { evento ->
                EventoItem(
                    evento = evento,
                    onComprarClick = {
                        navController.navigate("compra")
                    }
                )
            }
        }

        // FOOTER
        Text(
            text = "© 2025 Sweeper Tickets - Todos los derechos reservados",
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
