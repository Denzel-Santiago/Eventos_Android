//features/eventos/presentation/components/EventoAdminItem.kt

package com.proyecto.eventos.features.eventos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

@Composable
fun EventoAdminItem(
    evento: EventoEntidad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(evento.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Ubicación: ${evento.ubicacion}")
            Text("Fecha: ${evento.fecha}")
            Text("Boletos disponibles: ${evento.boletosDisponibles}")
            Text("Precio: $${evento.precio}")

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onEditar) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onEliminar) {
                    Text("Eliminar")
                }
            }
        }
    }
}