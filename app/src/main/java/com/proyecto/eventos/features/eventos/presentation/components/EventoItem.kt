//features/Eventos/presentation/components/EventoItem.kt
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
fun EventoItem(
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
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text("${evento.ubicacion} - ${evento.fecha}")
            Text("Boletos disponibles: ${evento.boletosDisponibles}")
            Text("Precio: $${evento.precio}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onComprarClick) {
                Text("Comprar Boletos")
            }
        }
    }
}