package com.proyecto.eventos.features.favoritos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

@Composable
fun FavoritoItem(
    evento: EventoEntidad,
    onEliminar: () -> Unit
) {
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = evento.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal
                )
                Text(
                    "${evento.fecha}  ${evento.hora}",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
                Text(
                    evento.ubicacion,
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
                Text(
                    "$${evento.precio}",
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar favorito",
                    tint = Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    }
}
