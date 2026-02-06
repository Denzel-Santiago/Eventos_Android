//features/Eventos/presentation/components/EventoCompraItem.kt
package com.proyecto.eventos.features.eventos.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.eventos.domain.entities.EventoCompraEntidad
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

@Composable
fun EventoCompraItem(
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
            Text(
                text = evento.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Ubicación: ${evento.ubicacion}", color = TextoSecundario)
            Text("Fecha: ${evento.fecha}", color = TextoSecundario)
            Text("Boletos disponibles: ${evento.boletosDisponibles}", color = TextoSecundario)
            Text("Precio: $${evento.precio}", color = TextoSecundario)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onComprarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdePrincipal,
                    contentColor = Color.Black
                )
            ) {
                Text("Comprar")
            }
        }
    }
}