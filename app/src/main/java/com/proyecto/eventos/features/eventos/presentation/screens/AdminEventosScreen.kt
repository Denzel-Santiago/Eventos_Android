//features/Eventos/presentation/screens/AdminEventosScreen.kt
package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Administrar Eventos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.abrirNuevoEvento() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Nuevo Evento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(eventos) { evento ->
                EventoAdminItem(
                    evento = evento,
                    onEditar = { viewModel.editarEvento(evento) },
                    onEliminar = { viewModel.eliminarEvento(evento.id) }
                )
            }
        }

        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (viewModel.eventoActual == null) "Nuevo Evento" else "Editar Evento"
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.ubicacion,
                    onValueChange = { viewModel.onUbicacionChange(it) },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.fecha,
                    onValueChange = { viewModel.onFechaChange(it) },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.boletos,
                    onValueChange = { viewModel.onBoletosChange(it) },
                    label = { Text("Boletos Disponibles") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.precio,
                    onValueChange = { viewModel.onPrecioChange(it) },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}