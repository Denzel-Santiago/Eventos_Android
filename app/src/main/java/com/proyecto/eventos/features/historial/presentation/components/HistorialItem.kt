package com.proyecto.eventos.features.historial.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.compras.domain.entities.CompraEntidad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialItem(compra: CompraEntidad) {
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    val fechaFormateada = try {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(compra.timestamp))
    } catch (e: Exception) {
        compra.fecha
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compra.nombreEvento,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal
                )
                Text(
                    text = "Fecha evento: ${compra.fecha}  ${compra.hora}",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
                Text(
                    text = "Dirección: ${compra.direccionEntrega}",
                    color = TextoSecundario.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Comprado: $fechaFormateada",
                        color = TextoSecundario.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$${compra.precio}",
                        color = VerdePrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
