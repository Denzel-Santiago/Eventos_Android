import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.Eventos.domain.entities.EventoAdminEntidad
import com.proyecto.eventos.features.Eventos.presentation.components.EventoAdminItem
import com.proyecto.eventos.features.Eventos.presentation.viewmodel.AdminEventosViewModel

@Composable
fun FormularioEventoDialog(
    evento: EventoAdminEntidad,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    onEventoCambiado: (EventoAdminEntidad) -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        },
        title = { Text("Evento") },
        text = {
            Column {
                OutlinedTextField(
                    value = evento.nombre,
                    onValueChange = { onEventoCambiado(evento.copy(nombre = it)) },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = evento.ubicacion,
                    onValueChange = { onEventoCambiado(evento.copy(ubicacion = it)) },
                    label = { Text("Ubicación") }
                )
                OutlinedTextField(
                    value = evento.fecha,
                    onValueChange = { onEventoCambiado(evento.copy(fecha = it)) },
                    label = { Text("Fecha") }
                )
                OutlinedTextField(
                    value = evento.precio.toString(),
                    onValueChange = {
                        onEventoCambiado(evento.copy(precio = it.toDoubleOrNull() ?: 0.0))
                    },
                    label = { Text("Precio") }
                )
                OutlinedTextField(
                    value = evento.boletosDisponibles.toString(),
                    onValueChange = {
                        onEventoCambiado(evento.copy(boletosDisponibles = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Boletos disponibles") }
                )
            }
        }
    )
}

@Composable
fun AdminEventosScreen(
    navController: NavController,
    viewModel: AdminEventosViewModel = viewModel()
) {
    val eventos by viewModel.eventos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gestión de Eventos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { viewModel.abrirNuevoEvento() }) {
                Text("Nuevo Evento")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(eventos) { evento ->
                EventoAdminItem(
                    evento = evento,
                    onEditar = { viewModel.editarEvento(evento) },
                    onEliminar = { viewModel.eliminarEvento(evento.id) }
                )
            }
        }

        viewModel.eventoActual?.let {
            if (viewModel.mostrarFormulario) {
                FormularioEventoDialog(
                    evento = it,
                    onGuardar = { viewModel.guardarEvento() },
                    onCancelar = { viewModel.cerrarFormulario() },
                    onEventoCambiado = { nuevo -> viewModel.eventoActual = nuevo }
                )
            }
        }
    }
}

