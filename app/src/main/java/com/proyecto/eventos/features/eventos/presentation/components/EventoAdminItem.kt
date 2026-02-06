//features/eventos/presentation/components/EventoAdminItem.kt

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
            Text("Ubicación: ${evento.ubicacion}", color = TextoSecundario)
            Text("Fecha: ${evento.fecha}", color = TextoSecundario)
            Text("Boletos disponibles: ${evento.boletosDisponibles}", color = TextoSecundario)
            Text("Precio: $${evento.precio}", color = TextoSecundario)

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 ACCIONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
