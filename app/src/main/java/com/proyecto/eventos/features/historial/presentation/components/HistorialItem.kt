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
    // Revertido a colores hardcodeados como estaba originalmente
    val verdePrincipal = Color(0xFF2DD4BF)
    val negroSuperficie = Color(0xFF1A1A1A)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)

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
        colors = CardDefaults.cardColors(containerColor = negroSuperficie.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
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
                    tint = verdePrincipal,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compra.nombreEvento,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdePrincipal
                )
                Text(
                    text = "Fecha evento: ${compra.fecha}  ${compra.hora}",
                    color = textoSecundario,
                    fontSize = 13.sp
                )
                Text(
                    text = "Dirección: ${compra.direccionEntrega}",
                    color = textoSecundario.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Comprado: $fechaFormateada",
                        color = textoSecundario.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$${compra.precio}",
                        color = verdePrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
