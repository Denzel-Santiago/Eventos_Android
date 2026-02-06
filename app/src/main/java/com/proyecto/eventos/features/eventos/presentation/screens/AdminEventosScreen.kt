//features/Eventos/presentation/screens/AdminEventosScreen.kt
package com.proyecto.eventos.features.eventos.presentation.screens

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
import com.proyecto.eventos.features.eventos.presentation.components.EventoAdminItem
import com.proyecto.eventos.features.eventos.presentation.viewmodel.AdminEventosViewModel
import com.proyecto.eventos.features.eventos.presentation.viewmodel.AdminEventosViewModelFactory

@Composable
fun AdminEventosScreen(
    navController: NavController,
    viewModel: AdminEventosViewModel = viewModel(factory = AdminEventosViewModelFactory())
) {
    val eventos by viewModel.eventos.collectAsStateWithLifecycle()

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
            text = "Administrar Eventos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 CTA NUEVO EVENTO
        Button(
            onClick = { viewModel.abrirNuevoEvento() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal,
                contentColor = Color.Black
            )
        ) {
            Text("Agregar nuevo evento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 LISTA
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(eventos) { evento ->
                EventoAdminItem(
                    evento = evento,
                    onEditar = { viewModel.editarEvento(evento) },
                    onEliminar = { viewModel.eliminarEvento(evento.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 REGRESAR
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

    if (viewModel.mostrarFormulario) {
        FormularioEventoDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.cerrarFormulario() },
            onGuardar = { viewModel.guardarEvento() }
        )
    }
}


@Composable
fun FormularioEventoDialog(
    viewModel: AdminEventosViewModel,
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
                text = if (viewModel.eventoActual == null)
                    "Nuevo Evento"
                else
                    "Editar Evento",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {

                OutlinedTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = viewModel.ubicacion,
                    onValueChange = { viewModel.onUbicacionChange(it) },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = viewModel.fecha,
                    onValueChange = { viewModel.onFechaChange(it) },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = viewModel.boletos,
                    onValueChange = { viewModel.onBoletosChange(it) },
                    label = { Text("Boletos disponibles") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = viewModel.precio,
                    onValueChange = { viewModel.onPrecioChange(it) },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )
            }
        },

        // 🔹 BOTONES ORDENADOS
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

@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF2DD4BF),
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    focusedLabelColor = Color(0xFF2DD4BF),
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
    focusedTextColor = Color(0xFFE5E7EB),
    unfocusedTextColor = Color(0xFFE5E7EB),
    cursorColor = Color(0xFF2DD4BF)
)
