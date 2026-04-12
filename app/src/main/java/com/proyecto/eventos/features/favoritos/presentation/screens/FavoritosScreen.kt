package com.proyecto.eventos.features.favoritos.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

    // Colores consistentes
    val negroSuperficie = Color(0xFF1A1A1A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)
    val errorColor = Color(0xFFEF4444)
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
                            verdePrincipal.copy(alpha = 0.05f),
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
                        spotColor = verdePrincipal.copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(containerColor = negroSuperficie.copy(alpha = 0.85f)),
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
                            color = verdePrincipal
                        )
                        Text(
                            text = "Eventos que te gustan",
                            fontSize = 12.sp,
                            color = textoSecundario
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
                                color = textoSecundario,
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
                                text = "No tienes favoritos aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = textoPrimario
                            )

                            Text(
                                text = "Agrega eventos desde Comprar Boletos",
                                fontSize = 14.sp,
                                color = textoSecundario,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { navController.navigate("eventos") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = verdePrincipal,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Explorar Eventos",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Lista de favoritos
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = favoritos,
                            key = { it.id }
                        ) { evento ->
                            FavoritoItemMejorado(
                                evento = evento,
                                isVisible = showContent,
                                index = favoritos.indexOf(evento),
                                verdePrincipal = verdePrincipal,
                                textoSecundario = textoSecundario,
                                textoPrimario = textoPrimario,
                                favoritoColor = favoritoColor,
                                errorColor = errorColor,
                                onEliminar = { viewModel.eliminarFavorito(evento.id) },
                                onClick = {
                                    // REDIRECCIÓN CORREGIDA A LA PANTALLA DE COMPRA (VERIFICACIÓN)
                                    navController.navigate("verificacion/${evento.id}/${evento.nombre}/${evento.fecha}/${evento.hora}/${evento.precio}")
                                }
                            )
                        }
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
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Regresar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritoItemMejorado(
    evento: EventoEntidad,
    isVisible: Boolean,
    index: Int,
    verdePrincipal: Color,
    textoSecundario: Color,
    textoPrimario: Color,
    favoritoColor: Color,
    errorColor: Color,
    onEliminar: () -> Unit,
    onClick: () -> Unit
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
                    elevation = 10.dp + (4.dp * glow),
                    shape = RoundedCornerShape(20.dp),
                    spotColor = favoritoColor.copy(alpha = 0.2f + (0.2f * glow))
                )
                .clickable {
                    isPressed = true
                    isFocused = true
                    onClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = favoritoColor.copy(alpha = 0.15f + (0.1f * glow))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = favoritoColor.copy(alpha = 0.2f)
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(bitmap),
                            contentDescription = evento.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Badge de favorito
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 5.dp, y = (-5).dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(favoritoColor)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF1A1A1A),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                } else {
                    // Placeholder
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2A2A2A),
                                        Color(0xFF1A1A1A)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = favoritoColor.copy(alpha = 0.3f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                }

                // Información del evento
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = evento.nombre,
                        color = textoPrimario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = textoSecundario,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evento.fecha,
                            color = textoSecundario,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = textoSecundario,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evento.hora,
                            color = textoSecundario,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = textoSecundario,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evento.ubicacion,
                            color = textoSecundario.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = verdePrincipal.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "$${evento.precio}",
                            color = verdePrincipal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Botón de eliminar
                IconButton(
                    onClick = {
                        isPressed = true
                        isFocused = true
                        onEliminar()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    errorColor.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar favorito",
                        tint = errorColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// También puedes mantener el FavoritoItem original si lo necesitas en otros lugares
@Composable
fun FavoritoItem(
    evento: EventoEntidad,
    onEliminar: () -> Unit
) {
    // Versión simplificada que usa el componente mejorado con valores por defecto
    FavoritoItemMejorado(
        evento = evento,
        isVisible = true,
        index = 0,
        verdePrincipal = Color(0xFF2DD4BF),
        textoSecundario = Color(0xFF9CA3AF),
        textoPrimario = Color(0xFFF9FAFB),
        favoritoColor = Color(0xFFFF6B6B),
        errorColor = Color(0xFFEF4444),
        onEliminar = onEliminar,
        onClick = { /* Opcional */ }
    )
}
