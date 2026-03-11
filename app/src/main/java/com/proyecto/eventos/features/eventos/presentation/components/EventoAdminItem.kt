//com.proyecto.eventos.features.eventos.presentation.components.EventoAdminItem.kt
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
fun EventoAdminItem(
    evento: EventoEntidad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(evento.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2DD4BF))
            Spacer(modifier = Modifier.height(4.dp))
            Text("${evento.fecha} ${evento.hora}", color = Color(0xFFE5E7EB), fontSize = 13.sp)
            Text("Ubicación: ${evento.ubicacion}", color = Color(0xFFE5E7EB), fontSize = 13.sp)
            Text("Stock: ${evento.stock}  |  Precio: $${evento.precio}", color = Color(0xFFE5E7EB), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditar, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2DD4BF), contentColor = Color.Black)) { Text("Editar") }
                OutlinedButton(onClick = onEliminar, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Eliminar") }
            }
        }
    }
}