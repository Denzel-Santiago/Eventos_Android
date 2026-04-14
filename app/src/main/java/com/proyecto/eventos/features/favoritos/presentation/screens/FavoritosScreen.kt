package com.proyecto.eventos.features.favoritos.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.favoritos.presentation.viewmodel.FavoritosViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    viewModel: FavoritosViewModel = hiltViewModel()
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(100)
            showContent = true
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val favoritoColor = Color(0xFFFF6B6B)

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "favoritos_animations")
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.background,
                        colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        colorScheme.background
                    )
                )
            )
    ) {
        // Efecto de luz ambiental
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Header mejorado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = colorScheme.primary.copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Mis Favoritos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.primary
                        )
                        Text(
                            text = "Eventos que te gustan",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    // Contador de favoritos
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        favoritoColor.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = favoritos.size.toString(),
                                color = favoritoColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            when {
                isLoading && favoritos.isEmpty() -> {
                    // Pantalla de carga mejorada
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Card(
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(1f + (floatAnimation / 100f))
                                    .shadow(
                                        elevation = 20.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        spotColor = favoritoColor.copy(alpha = 0.5f)
                                    ),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = favoritoColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = favoritoColor,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando favoritos...",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                favoritos.isEmpty() -> {
                    // Estado vacío mejorado
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(
                                        elevation = 16.dp,
                                        shape = RoundedCornerShape(24.dp),
                                        spotColor = favoritoColor.copy(alpha = 0.3f)
                                    ),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No tienes favoritos aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )

                            Text(
                                text = "Agrega eventos desde Comprar Boletos",
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { navController.navigate("eventos") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary,
                                    contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                            ) {
                                Text("Explorar Eventos", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = favoritos,
                            key = { it.id }
                        ) { evento ->
                            FavoritoItemCard(
                                evento = evento,
                                onRemove = { viewModel.eliminarFavorito(evento.id) },
                                onDetails = {
                                    val ruta = "verificacion/${evento.id}/${evento.nombre}/${evento.fecha}/${evento.hora}/${evento.precio.toFloat()}"
                                    navController.navigate(ruta)
                                },
                                isVisible = showContent,
                                index = favoritos.indexOf(evento)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Regresar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Regresar al Panel", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FavoritoItemCard(
    evento: EventoEntidad,
    onRemove: () -> Unit,
    onDetails: () -> Unit,
    isVisible: Boolean,
    index: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400 + (index * 100))
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDetails() }
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen del evento
                val bitmap = remember(evento.imagen) {
                    if (evento.imagen.isNotBlank()) {
                        try {
                            val bytes = android.util.Base64.decode(evento.imagen, android.util.Base64.DEFAULT)
                            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch (e: Exception) { null }
                    } else null
                }

                if (bitmap != null) {
                    Image(
                        painter = rememberAsyncImagePainter(bitmap),
                        contentDescription = evento.nombre,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Event, null, tint = colorScheme.primary.copy(alpha = 0.3f))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = evento.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${evento.fecha} • ${evento.hora}",
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = evento.ubicacion,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Favorite, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B))
                }
            }
        }
    }
}
