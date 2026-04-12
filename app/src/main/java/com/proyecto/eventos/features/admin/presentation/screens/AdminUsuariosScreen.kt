package com.proyecto.eventos.features.admin.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import com.proyecto.eventos.features.admin.presentation.viewmodel.AdminUsuariosViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    viewModel: AdminUsuariosViewModel = hiltViewModel()
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val mensajeError by viewModel.mensajeError.collectAsStateWithLifecycle()
    val usuarioAEliminar by viewModel.usuarioAEliminar.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(100)
            showContent = true
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(label = "admin_usuarios_animations")
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    if (usuarioAEliminar != null) {
        DialogEliminarUsuario(
            usuario = usuarioAEliminar!!,
            onConfirm = { viewModel.confirmarEliminar() },
            onDismiss = { viewModel.cancelarEliminar() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Gestión de Usuarios",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.primary
                        )
                        Text(
                            text = "Administra las cuentas del sistema",
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        colorScheme.primary.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
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
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        EstadisticaUsuarioItem(
                            icono = Icons.Default.People,
                            valor = usuarios.size.toString(),
                            etiqueta = "Total",
                            color = colorScheme.primary
                        )

                        EstadisticaUsuarioItem(
                            icono = Icons.Default.AdminPanelSettings,
                            valor = usuarios.count { it.rol.lowercase() == "admin" }.toString(),
                            etiqueta = "Admins",
                            color = Color(0xFFFFA500)
                        )

                        EstadisticaUsuarioItem(
                            icono = Icons.Default.Person,
                            valor = usuarios.count { it.rol.lowercase() != "admin" }.toString(),
                            etiqueta = "Usuarios",
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }

                if (isLoading && usuarios.isEmpty()) {
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
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando usuarios...",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else if (usuarios.isEmpty()) {
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
                                text = "No hay usuarios registrados",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Los usuarios aparecerán aquí cuando se registren",
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Total de usuarios",
                                        color = colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    color = colorScheme.primary,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = usuarios.size.toString(),
                                        color = colorScheme.onPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = usuarios,
                                key = { it.uid }
                            ) { usuario ->
                                UsuarioAdminCardMejorada(
                                    usuario = usuario,
                                    isVisible = showContent,
                                    index = usuarios.indexOf(usuario),
                                    onEliminar = { viewModel.pedirConfirmacionEliminar(usuario) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticaUsuarioItem(
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
            fontSize = 18.sp,
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
fun DialogEliminarUsuario(
    usuario: UsuarioAdminEntidad,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .scale(scale)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = colorScheme.error.copy(alpha = 0.3f)
                ),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(colorScheme.error.copy(alpha = 0.1f))
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Eliminar usuario",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Seguro que deseas eliminar a",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = colorScheme.error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "\"${usuario.nombre}\"",
                        color = colorScheme.error,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Text(
                    text = "Esta acción no se puede deshacer.",
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
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
                        Text("Cancelar", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            isPressed = true
                            onConfirm()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eliminar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioAdminCardMejorada(
    usuario: UsuarioAdminEntidad,
    isVisible: Boolean,
    index: Int,
    onEliminar: () -> Unit
) {
    val esAdmin = usuario.rol.lowercase() == "admin"
    var isPressed by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

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
                    spotColor = if (esAdmin)
                        colorScheme.primary.copy(alpha = 0.2f + (0.2f * glow))
                    else
                        colorScheme.onSurface.copy(alpha = 0.1f + (0.1f * glow))
                ),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (esAdmin)
                    colorScheme.primary.copy(alpha = 0.2f + (0.1f * glow))
                else
                    colorScheme.onSurface.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = if (esAdmin)
                                colorScheme.primary.copy(alpha = 0.3f)
                            else
                                colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        .background(
                            brush = if (esAdmin) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorScheme.primary,
                                        colorScheme.primaryContainer
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorScheme.secondaryContainer,
                                        colorScheme.surface
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                        color = if (esAdmin) colorScheme.onPrimary else colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )

                    if (esAdmin) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                                .border(
                                    width = 2.dp,
                                    color = colorScheme.surface,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = usuario.nombre,
                        color = if (esAdmin) colorScheme.primary else colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (usuario.email.isNotBlank()) {
                        Text(
                            text = usuario.email,
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = if (esAdmin)
                            colorScheme.primary.copy(alpha = 0.15f)
                        else
                            colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            if (esAdmin) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (esAdmin) "ADMINISTRADOR" else "USUARIO",
                                color = if (esAdmin) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!esAdmin) {
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
                                        colorScheme.error.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar usuario",
                            tint = colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Protegido",
                            tint = colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}