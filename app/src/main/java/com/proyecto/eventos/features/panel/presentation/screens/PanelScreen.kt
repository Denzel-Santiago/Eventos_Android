package com.proyecto.eventos.features.panel.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.panel.presentation.viewmodel.PanelViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelScreen(
    navController: NavController,
    viewModel: PanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accion by viewModel.accion.collectAsStateWithLifecycle()
    var isFocused by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            delay(100)
            showMenu = true
        }
    }

    LaunchedEffect(accion) {
        when (accion) {
            is PanelViewModel.PanelAccion.Logout -> {
                navController.navigate("login") {
                    popUpTo("panel") { inclusive = true }
                }
                viewModel.limpiarAccion()
            }
            null -> {}
        }
    }

    val negroFondo = Color(0xFF0A0A0A)
    val negroSuperficie = Color(0xFF1A1A1A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)

    val infiniteTransition = rememberInfiniteTransition(label = "panel_animations")

    val rotation by animateFloatAsState(
        targetValue = if (uiState.isLoading) 360f else 0f,
        animationSpec = if (uiState.isLoading) {
            infiniteRepeatable(animation = tween(1000, easing = LinearEasing))
        } else {
            tween(300)
        },
        label = "rotation"
    )

    val loadingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Sweeper Tickets",
                            color = verdePrincipal,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Bienvenido",
                            fontSize = 12.sp,
                            color = textoSecundario
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = { viewModel.refreshUserData() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        verdePrincipal.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = verdePrincipal,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotation)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Red.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        if (uiState.isLoading && uiState.userName.isNotEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = verdePrincipal,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.Red.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = negroSuperficie)
            )
        }
    ) { paddingValues ->

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

            if (uiState.isLoading && uiState.userName.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = verdePrincipal,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        CircularProgressIndicator(
                            color = verdePrincipal,
                            strokeWidth = 3.dp
                        )
                    }
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(
                                elevation = 14.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = verdePrincipal.copy(alpha = 0.3f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = negroSuperficie),
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .shadow(
                                        elevation = 10.dp,
                                        shape = CircleShape,
                                        spotColor = verdePrincipal.copy(alpha = 0.5f)
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                verdePrincipal,
                                                verdePrincipal.copy(alpha = 0.7f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = uiState.userName.take(1).uppercase().ifEmpty { "?" },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    text = uiState.welcomeMessage,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textoPrimario,
                                    maxLines = 1
                                )

                                Text(
                                    text = uiState.userEmail,
                                    fontSize = 14.sp,
                                    color = textoSecundario,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item {
                            MenuItemAnimation(isVisible = showMenu, index = 0) {
                                OpcionMenuPrincipal(
                                    titulo = "Comprar Boletos",
                                    descripcion = "Explora eventos disponibles",
                                    icono = Icons.Default.ShoppingCart,
                                    colorIcono = verdePrincipal,
                                    onClick = { navController.navigate("eventos") }
                                )
                            }
                        }

                        item {
                            MenuItemAnimation(isVisible = showMenu, index = 1) {
                                OpcionMenuPrincipal(
                                    titulo = "Mis Favoritos",
                                    descripcion = "Eventos guardados",
                                    icono = Icons.Default.Favorite,
                                    colorIcono = Color(0xFFFF6B6B),
                                    onClick = { navController.navigate("favoritos") }
                                )
                            }
                        }

                        item {
                            MenuItemAnimation(isVisible = showMenu, index = 2) {
                                OpcionMenuPrincipal(
                                    titulo = "Historial de Compras",
                                    descripcion = "Tus compras anteriores",
                                    icono = Icons.Default.History,
                                    colorIcono = Color(0xFFFFA500),
                                    onClick = { navController.navigate("historial") }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Divider(
                            color = Color.White.copy(alpha = 0.08f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Sweeper Tickets",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )

                        Text(
                            text = "Versión 2.0.0",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemAnimation(
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

@Composable
fun OpcionMenuPrincipal(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit
) {

    var isPressed by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "click_scale"
    )

    val glow by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(300),
        label = "glow_anim"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(120)
            isPressed = false
            isFocused = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 14.dp + (6.dp * glow),
                shape = RoundedCornerShape(16.dp),
                spotColor = colorIcono.copy(alpha = 0.3f + (0.4f * glow)),
                ambientColor = colorIcono.copy(alpha = 0.1f + (0.2f * glow))
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f) // efecto glass
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = colorIcono.copy(alpha = 0.25f)
        ),
        onClick = {
            isPressed = true
            isFocused = true
            onClick()
        }
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorIcono.copy(alpha = 0.18f),
                                colorIcono.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    text = descripcion,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorIcono.copy(alpha = 0.5f)
            )
        }
    }
}