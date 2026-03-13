package com.proyecto.eventos.features.eventos.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.proyecto.eventos.features.eventos.presentation.viewmodel.CompraViewModel
import com.proyecto.eventos.features.favoritos.presentation.viewmodel.FavoritosViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CompraListScreen(
    navController: NavController,
    compraViewModel: CompraViewModel = hiltViewModel(),
    favoritosViewModel: FavoritosViewModel = hiltViewModel()
) {
    val eventos by compraViewModel.eventos.collectAsStateWithLifecycle()
    val isLoading by compraViewModel.isLoading.collectAsStateWithLifecycle()
    val favoritos by favoritosViewModel.favoritos.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Estado para animación de entrada
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(100)
            showContent = true
        }
    }

    // Colores consistentes con el panel
    val negroFondo = Color(0xFF0A0A0A)
    val negroSuperficie = Color(0xFF1A1A1A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)
    val errorColor = Color(0xFFEF4444)

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "eventos_animations")
    val loadingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050505),
                        Color(0xFF0F1F1D),
                        Color(0xFF000000)
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
                            verdePrincipal.copy(alpha = 0.08f),
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
            // Header con título y contador
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = verdePrincipal.copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(containerColor = negroSuperficie.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Eventos Disponibles",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdePrincipal,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Encuentra tu próximo evento",
                            fontSize = 12.sp,
                            color = textoSecundario
                        )
                    }

                    // Contador de eventos con animación
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        verdePrincipal.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = eventos.size.toString(),
                                color = verdePrincipal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            if (isLoading && eventos.isEmpty()) {
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
                                .scale(loadingScale)
                                .shadow(
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = verdePrincipal.copy(alpha = 0.5f)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = verdePrincipal.copy(alpha = 0.1f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = verdePrincipal,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando eventos...",
                            color = textoSecundario,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            color = verdePrincipal,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            } else if (eventos.isEmpty()) {
                // Pantalla vacía mejorada
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
                                .size(100.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = textoSecundario.copy(alpha = 0.3f)
                                ),
                            colors = CardDefaults.cardColors(containerColor = negroSuperficie),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = textoSecundario,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay eventos disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = textoPrimario
                        )
                        Text(
                            text = "Vuelve más tarde para ver novedades",
                            fontSize = 14.sp,
                            color = textoSecundario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista de eventos con animaciones
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = eventos,
                        key = { it.id }
                    ) { evento ->
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
                            isVisible = showContent,
                            index = eventos.indexOf(evento),
                            verdePrincipal = verdePrincipal,
                            textoSecundario = textoSecundario,
                            textoPrimario = textoPrimario,
                            negroSuperficie = negroSuperficie,
                            errorColor = errorColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de regreso mejorado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = verdePrincipal.copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(containerColor = negroSuperficie.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textoSecundario
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = verdePrincipal.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Regresar al Panel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EventoCompraCard(
    evento: EventoEntidad,
    esFavorito: Boolean,
    onFavoritoClick: (Boolean) -> Unit,
    onComprarClick: () -> Unit,
    isVisible: Boolean,
    index: Int,
    verdePrincipal: Color,
    textoSecundario: Color,
    textoPrimario: Color,
    negroSuperficie: Color,
    errorColor: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val glow by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(300),
        label = "glow_anim"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
            isFocused = false
        }
    }

    // Animación de entrada
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400 + (index * 100), easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400 + (index * 100)))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(
                    elevation = 12.dp + (4.dp * glow),
                    shape = RoundedCornerShape(20.dp),
                    spotColor = verdePrincipal.copy(alpha = 0.3f + (0.3f * glow)),
                    ambientColor = verdePrincipal.copy(alpha = 0.1f + (0.1f * glow))
                ),
            colors = CardDefaults.cardColors(
                containerColor = negroSuperficie.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = verdePrincipal.copy(alpha = 0.15f + (0.1f * glow))
            )
        ) {
            Column {
                // Imagen del evento (Base64)
                val bitmap = remember(evento.imagen) {
                    if (evento.imagen.isNotBlank()) {
                        try {
                            val bytes = android.util.Base64.decode(evento.imagen, android.util.Base64.DEFAULT)
                            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }

                if (bitmap != null) {
                    Image(
                        painter = rememberAsyncImagePainter(bitmap),
                        contentDescription = evento.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Badge de stock si es bajo
                    if (evento.stock < 10) {
                        Box(
                            modifier = Modifier
                                .offset(x = 12.dp, y = (-30).dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    spotColor = errorColor.copy(alpha = 0.3f)
                                )
                        ) {
                            Surface(
                                color = errorColor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "¡Últimas ${evento.stock} unidades!",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Placeholder si no hay imagen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        negroSuperficie,
                                        negroSuperficie.copy(alpha = 0.5f)
                                    )
                                )
                            )
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = verdePrincipal.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Título y ubicación
                    Text(
                        text = evento.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = verdePrincipal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fecha y hora
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = textoSecundario,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${evento.fecha} • ${evento.hora}",
                            color = textoSecundario,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Ubicación
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = textoSecundario,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evento.ubicacion,
                            color = textoSecundario.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stock y precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = verdePrincipal.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Stock: ${evento.stock}",
                                color = textoPrimario,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = "$${evento.precio}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdePrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón de favorito
                        OutlinedButton(
                            onClick = {
                                isPressed = true
                                isFocused = true
                                onFavoritoClick(esFavorito)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (esFavorito) errorColor else textoSecundario
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (esFavorito)
                                    errorColor.copy(alpha = 0.5f)
                                else
                                    verdePrincipal.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (esFavorito) "Guardado" else "Favorito",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Botón de comprar
                        Button(
                            onClick = {
                                isPressed = true
                                isFocused = true
                                onComprarClick()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = verdePrincipal,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = evento.stock > 0
                        ) {
                            Text(
                                "Comprar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Mensaje si no hay stock
                    if (evento.stock <= 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Evento agotado",
                            color = errorColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}