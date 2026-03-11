package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModel
import com.proyecto.eventos.features.favoritos.presentation.viewmodel.FavoritosViewModel

@Composable
fun CompraListScreen(
    navController: NavController,
    compraViewModel: CompraViewModel = hiltViewModel(),
    favoritosViewModel: FavoritosViewModel = hiltViewModel()
) {
    val eventos by compraViewModel.eventos.collectAsStateWithLifecycle()
    val isLoading by compraViewModel.isLoading.collectAsStateWithLifecycle()
    val favoritos by favoritosViewModel.favoritos.collectAsStateWithLifecycle()

    val NegroFondo = Color(0xFF0A0A0A)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroFondo)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Eventos Disponibles",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdePrincipal)
                }
            }
            eventos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay eventos disponibles", color = TextoSecundario)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(eventos) { evento ->
                        EventoCompraCard(
                            evento = evento,
                            esFavorito = favoritos.any { it.id == evento.id },
                            onFavoritoClick = { esFav ->
                                if (esFav) {
                                    favoritosViewModel.eliminarFavorito(evento.id)
                                } else {
                                    favoritosViewModel.agregarFavorito(evento)
                                }
                            },
                            onComprarClick = {
                                val ruta = "verificacion/${evento.id}/${evento.nombre}/${evento.fecha}/${evento.hora}/${evento.precio.toFloat()}"
                                navController.navigate(ruta)
                            },
                            verdePrincipal = VerdePrincipal,
                            textoSecundario = TextoSecundario
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoSecundario)
        ) {
            Text("Regresar")
        }
    }
}

@Composable
fun EventoCompraCard(
    evento: EventoEntidad,
    esFavorito: Boolean,
    onFavoritoClick: (Boolean) -> Unit,
    onComprarClick: () -> Unit,
    verdePrincipal: Color,
    textoSecundario: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = evento.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = verdePrincipal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${evento.fecha}  ${evento.hora}", color = textoSecundario, fontSize = 13.sp)
            Text("Ubicación: ${evento.ubicacion}", color = textoSecundario, fontSize = 13.sp)
            Text(
                "Stock: ${evento.stock}  |  Precio: $${evento.precio}",
                color = textoSecundario,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onFavoritoClick(esFavorito) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (esFavorito) Color.Red else Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (esFavorito) "Guardado" else "Favorito")
                }
                Button(
                    onClick = onComprarClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Comprar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}