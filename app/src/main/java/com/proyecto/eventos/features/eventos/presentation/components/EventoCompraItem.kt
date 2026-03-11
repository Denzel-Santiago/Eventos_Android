//features/Eventos/presentation/components/EventoCompraItem.kt
package com.proyecto.eventos.features.eventos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

@Composable
fun EventoCompraItem(
    evento: EventoEntidad,
    onComprarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(evento.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Ubicación: ${evento.ubicacion}", color = Color(0xFFE5E7EB))
            Text("Fecha: ${evento.fecha}  ${evento.hora}", color = Color(0xFFE5E7EB))
            Text("Stock: ${evento.stock}", color = Color(0xFFE5E7EB))
            Text("Precio: $${evento.precio}", color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onComprarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2DD4BF), contentColor = Color.Black)
            ) { Text("Comprar") }
        }
    }
}