//features/Eventos/presentation/screens/CompraScreen.kt
package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.presentation.components.EventoCompraItem
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModel
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModelFactory
@Composable
fun CompraScreen(
    navController: NavController,
    viewModel: CompraViewModel = viewModel(factory = CompraViewModelFactory())
) {

    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val NegroFondo = Color(0xFF0A0A0A)
    val NegroContenedor = Color(0xFF111111)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val VerdeHover = Color(0xFF14B8A6)
    val TextoSecundario = Color(0xFFE5E7EB)
    val Blanco10 = Color.White.copy(alpha = 0.1f)


    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroFondo)
            .statusBarsPadding() // ✅ Respeta barra de estado
            .padding(16.dp)
    ) {

        // 🔹 TÍTULO
        Text(
            text = "Eventos Disponibles",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 LISTA
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(eventos) { evento ->
                EventoCompraItem(
                    evento = evento,
                    onComprarClick = {
                        viewModel.comprarBoleto(evento)
                    }
                )
            }
        }

        // 🔹 MENSAJE
        mensaje?.let {
            AlertDialog(
                containerColor = NegroContenedor,
                titleContentColor = VerdePrincipal,
                textContentColor = TextoSecundario,
                onDismissRequest = { viewModel.limpiarMensaje() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.limpiarMensaje() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal
                        )
                    ) {
                        Text("Aceptar", color = Color.Black)
                    }
                },
                title = { Text("Compra exitosa") },
                text = { Text(it) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 BOTÓN REGRESAR
        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = VerdePrincipal
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            )
        ) {
            Text("Regresar al inicio")
        }
    }
}



