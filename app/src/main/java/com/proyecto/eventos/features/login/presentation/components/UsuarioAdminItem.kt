//features/login/presentation/components/UsuarioAdminItem.kt
package com.proyecto.eventos.features.login.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

@Composable
fun UsuarioAdminItem(
    usuario: UsuarioEntidad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(usuario.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Email: ${usuario.email}")
            Text("Rol: ${usuario.role}")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onEditar) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onEliminar) {
                    Text("Eliminar")
                }
            }
        }
    }
}