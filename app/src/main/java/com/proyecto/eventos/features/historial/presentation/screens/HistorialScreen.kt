package com.proyecto.eventos.features.historial.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.historial.presentation.components.HistorialItem
import com.proyecto.eventos.features.historial.presentation.viewmodel.HistorialViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val historial by viewModel.historial.collectAsStateWithLifecycle()
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
    val totalGastado = historial.sumOf { it.precio }

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "historial_animations")
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
                            text = "Historial de Compras",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.primary
                        )
                        Text(
                            text = "Tus boletos adquiridos",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    // Contador de compras
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        colorScheme.primary.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = historial.size.toString(),
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Estadísticas rápidas
            if (historial.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = colorScheme.primary.copy(alpha = 0.15f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        EstadisticaItem(
                            icono = Icons.Default.ShoppingCart,
                            valor = historial.size.toString(),
                            etiqueta = "Compras",
                            color = colorScheme.primary
                        )

                        EstadisticaItem(
                            icono = Icons.Default.ConfirmationNumber,
                            valor = historial.size.toString(),
                            etiqueta = "Boletos",
                            color = Color(0xFFFFA500)
                        )

                        EstadisticaItem(
                            icono = Icons.Default.AttachMoney,
                            valor = "$${totalGastado}",
                            etiqueta = "Gastado",
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }
            }

            when {
                isLoading && historial.isEmpty() -> {
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
                                        spotColor = colorScheme.primary.copy(alpha = 0.5f)
                                    ),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando historial...",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                historial.isEmpty() -> {
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
                                        spotColor = colorScheme.onSurface.copy(alpha = 0.3f)
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
                                text = "No tienes compras aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )

                            Text(
                                text = "Tus boletos comprados aparecerán aquí",
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
                    // Lista de historial con animación de entrada
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = historial,
                            key = { it.id }
                        ) { compra ->
                            AnimatedItem(
                                isVisible = showContent,
                                index = historial.indexOf(compra)
                            ) {
                                HistorialItem(compra = compra)
                            }
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
                        spotColor = colorScheme.primary.copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colorScheme.primary.copy(alpha = 0.3f)
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
fun EstadisticaItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valor: String,
    etiqueta: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = etiqueta,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun AnimatedItem(
    isVisible: Boolean,
    index: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400 + (index * 100), easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400 + (index * 100)))
    ) {
        content()
    }
}