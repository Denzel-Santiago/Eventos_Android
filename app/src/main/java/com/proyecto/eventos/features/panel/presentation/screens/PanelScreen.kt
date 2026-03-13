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
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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

    val negroSuperficie = Color(0xFF1A1A1A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)

    val infiniteTransition = rememberInfiniteTransition(label = "panel_anim")

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
                            text = if (uiState.isAdmin) "Panel de Administrador" else "Panel de Usuario",
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
                                        verdePrincipal.copy(alpha = 0.25f),
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
                                Brush.radialGradient(
                                    listOf(Color.Red.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.Red
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = negroSuperficie
                )
            )
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
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
                                .scale(loadingScale),
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
                                    Icons.Default.ConfirmationNumber,
                                    null,
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
                        colors = CardDefaults.cardColors(
                            containerColor = negroSuperficie
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                verdePrincipal,
                                                verdePrincipal.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                                    .shadow(
                                        10.dp,
                                        CircleShape,
                                        spotColor = verdePrincipal.copy(alpha = 0.4f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    uiState.userName.take(1).uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    uiState.welcomeMessage,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textoPrimario
                                )

                                Text(
                                    uiState.userEmail,
                                    fontSize = 14.sp,
                                    color = textoSecundario
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

                        if (uiState.isAdmin) {

                            item {

                                Text(
                                    "Administración",
                                    color = verdePrincipal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            item {

                                MenuItemAnimation(showMenu, 0) {

                                    OpcionMenuPrincipal(
                                        "Gestionar Usuarios",
                                        "Ver, editar o eliminar usuarios",
                                        Icons.Default.People,
                                        Color(0xFF60A5FA)
                                    ) {
                                        navController.navigate("admin/usuarios")
                                    }
                                }
                            }

                            item {

                                MenuItemAnimation(showMenu, 1) {

                                    OpcionMenuPrincipal(
                                        "Gestionar Eventos",
                                        "Crear o modificar la cartelera",
                                        Icons.Default.Event,
                                        Color(0xFFF472B6)
                                    ) {
                                        navController.navigate("admin/eventos")
                                    }
                                }
                            }

                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color.White.copy(alpha = 0.1f)
                                )
                            }
                        }

                        item {

                            Text(
                                "Eventos y Boletos",
                                color = verdePrincipal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        item {

                            MenuItemAnimation(showMenu, 2) {

                                OpcionMenuPrincipal(
                                    "Comprar Boletos",
                                    "Explora eventos disponibles",
                                    Icons.Default.ShoppingCart,
                                    verdePrincipal
                                ) {
                                    navController.navigate("eventos")
                                }
                            }
                        }

                        item {

                            MenuItemAnimation(showMenu, 3) {

                                OpcionMenuPrincipal(
                                    "Mis Favoritos",
                                    "Eventos guardados",
                                    Icons.Default.Favorite,
                                    Color(0xFFFF6B6B)
                                ) {
                                    navController.navigate("favoritos")
                                }
                            }
                        }

                        item {

                            MenuItemAnimation(showMenu, 4) {

                                OpcionMenuPrincipal(
                                    "Historial de Compras",
                                    "Tus compras anteriores",
                                    Icons.Default.History,
                                    Color(0xFFFFA500)
                                ) {
                                    navController.navigate("historial")
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            "Sweeper Tickets • Versión 2.0.0",
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
            animationSpec = tween(400 + (index * 50))
        ) + fadeIn()
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
        label = "scale"
    )

    val glow by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(300),
        label = "glow"
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
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            colorIcono.copy(alpha = 0.25f)
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
                        Brush.linearGradient(
                            listOf(
                                colorIcono.copy(alpha = 0.18f),
                                colorIcono.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    icono,
                    null,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    descripcion,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = colorIcono.copy(alpha = 0.5f)
            )
        }
    }
}