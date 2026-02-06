//features/Eventos/presentation/components/EventoItem.kt
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
fun EventoItem(
    evento: EventoEntidad,
    onComprarClick: () -> Unit
) {
    val NegroContenedor = Color(0xFF111111)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = NegroContenedor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 🔹 TÍTULO
            Text(
                text = evento.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = VerdePrincipal
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 🔹 INFO
            Text(
                text = "${evento.ubicacion} • ${evento.fecha}",
                color = TextoSecundario,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Boletos disponibles: ${evento.boletosDisponibles}",
                color = TextoSecundario,
                fontSize = 14.sp
            )

            Text(
                text = "Precio: $${evento.precio}",
                color = TextoSecundario,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 CTA
            Button(
                onClick = onComprarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdePrincipal,
                    contentColor = Color.Black
                )
            ) {
                Text("Comprar boletos")
            }
        }
    }
}
