//com.proyecto.eventos.features.eventos.presentation.components.EventoItem.kt
package com.proyecto.eventos.features.eventos.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

@Composable
fun EventoItem(
    evento: EventoEntidad,
    onComprarClick: () -> Unit
) {
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column {
            // Imagen del evento (Base64)
            if (evento.imagen.isNotBlank()) {
                val bitmap = remember(evento.imagen) {
                    try {
                        val bytes = android.util.Base64.decode(evento.imagen, android.util.Base64.DEFAULT)
                        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        painter = rememberAsyncImagePainter(bitmap),
                        contentDescription = evento.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(evento.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = VerdePrincipal)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${evento.ubicacion} • ${evento.fecha}", color = TextoSecundario, fontSize = 14.sp)
                Text("Stock: ${evento.stock}", color = TextoSecundario, fontSize = 14.sp)
                Text("Precio: $${evento.precio}", color = TextoSecundario, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onComprarClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal, contentColor = Color.Black)
                ) { Text("Comprar boletos") }
            }
        }
    }
}
