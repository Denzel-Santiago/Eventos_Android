package com.proyecto.eventos.features.compras.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.proyecto.eventos.features.compras.presentation.viewmodel.ComprasViewModel
import java.io.File
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun VerificacionIdentidadScreen(
    navController: NavController,
    eventoId: String,
    nombreEvento: String,
    fecha: String,
    hora: String,
    precio: Double,
    viewModel: ComprasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Colores consistentes
    val negroFondo = Color(0xFF0A0A0A)
    val negroSuperficie = Color(0xFF1A1A1A)
    val verdePrincipal = Color(0xFF2DD4BF)
    val textoSecundario = Color(0xFF9CA3AF)
    val textoPrimario = Color(0xFFF9FAFB)
    val errorColor = Color(0xFFEF4444)
    val errorClaro = Color(0xFFFEE2E2)
    val warningColor = Color(0xFFFFA500)
    val successColor = Color(0xFF10B981)

    var mostrarCamara by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCameraInitialized by remember { mutableStateOf(false) }

    // Estados para animaciones
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "verificacion_animations")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Permiso cámara
    var tieneCameraPermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        tieneCameraPermiso = granted
        if (granted) mostrarCamara = true
    }

    // Permiso GPS
    var tieneLocationPermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tieneLocationPermiso = granted }

    // Permiso notificaciones
    var tieneNotificacionPermiso by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED
            else true
        )
    }
    val notificacionPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tieneNotificacionPermiso = granted }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !tieneNotificacionPermiso) {
            notificacionPermisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(uiState.compraExitosa) {
        if (uiState.compraExitosa) {
            navController.navigate("panel") {
                popUpTo("panel") { inclusive = false }
            }
        }
    }

    // ── Diálogo de confirmación de texto OCR MEJORADO ───────────────────
    if (uiState.mostrandoConfirmacion) {
        Dialog(
            onDismissRequest = { viewModel.rechazarINE() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = verdePrincipal.copy(alpha = 0.3f)
                    ),
                colors = CardDefaults.cardColors(containerColor = negroSuperficie),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            verdePrincipal.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = verdePrincipal,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Verificar Identidad",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = verdePrincipal
                            )
                            Text(
                                text = "¿Es tu INE?",
                                fontSize = 14.sp,
                                color = textoSecundario
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Texto detectado
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = verdePrincipal.copy(alpha = 0.1f)
                            ),
                        color = Color(0xFF0D0D0D),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Texto detectado:",
                                fontSize = 12.sp,
                                color = textoSecundario.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.textoDetectado.take(500),
                                fontSize = 13.sp,
                                color = textoPrimario,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Indicador de nombre encontrado
                    AnimatedContent(
                        targetState = uiState.nombreEncontradoEnINE,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "nombre_encontrado"
                    ) { encontrado ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (encontrado)
                                verdePrincipal.copy(alpha = 0.1f)
                            else
                                warningColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (encontrado)
                                        Icons.Default.CheckCircle
                                    else
                                        Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (encontrado) successColor else warningColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (encontrado)
                                        "Tu nombre fue encontrado en la INE"
                                    else
                                        "Nombre no detectado automáticamente",
                                    color = if (encontrado) successColor else warningColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¿El texto corresponde a tu credencial de elector?",
                        fontSize = 15.sp,
                        color = textoPrimario,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.rechazarINE() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = errorColor
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = errorColor.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rechazar", fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = { viewModel.confirmarINE() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = verdePrincipal,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirmar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── Vista de cámara MEJORADA ────────────────────────────────────────
    if (mostrarCamara) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

            DisposableEffect(lifecycleOwner) {
                onDispose {
                    try {
                        cameraProviderFuture.get()?.unbindAll()
                    } catch (e: Exception) { }
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build()
                                .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                            val capture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = capture
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview, capture
                            )
                            isCameraInitialized = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )

            // Overlay con guía
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                // Marco guía para la INE
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.9f)
                        .aspectRatio(0.63f) // Proporción de una INE
                        .border(
                            width = 3.dp,
                            color = verdePrincipal,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // Instrucción superior
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = verdePrincipal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Coloca tu INE dentro del marco y toma la foto",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Botón tomar foto
                Button(
                    onClick = {
                        val capture = imageCapture ?: return@Button
                        val file = File(context.filesDir, "ines/${System.currentTimeMillis()}.jpg")
                            .also { it.parentFile?.mkdirs() }

                        capture.takePicture(
                            ImageCapture.OutputFileOptions.Builder(file).build(),
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    mostrarCamara = false
                                    viewModel.analizarFotoINE(file.absolutePath)
                                }
                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                    mostrarCamara = false
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(32.dp)
                        .size(80.dp)
                        .scale(if (isCameraInitialized) pulseScale else 1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verdePrincipal,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Botón cerrar
                IconButton(
                    onClick = {
                        try {
                            cameraProviderFuture.get()?.unbindAll()
                        } catch (e: Exception) { }
                        mostrarCamara = false
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancelar",
                        tint = Color.White
                    )
                }
            }
        }

    } else {
        // ── Vista principal MEJORADA ─────────────────────────────────────
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header con info del evento
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = verdePrincipal.copy(alpha = 0.2f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = negroSuperficie.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Verificación de Identidad",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdePrincipal
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = verdePrincipal.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = nombreEvento,
                                color = textoPrimario,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = textoSecundario,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$fecha",
                                    color = textoSecundario,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = textoSecundario,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = hora,
                                    color = textoSecundario,
                                    fontSize = 13.sp
                                )
                            }

                            Surface(
                                color = verdePrincipal,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "$${precio.toInt()}",
                                    color = Color.Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PASO 1 — Foto INE con OCR
                PasoCardMejorado(
                    numero = "1",
                    titulo = "Foto de INE",
                    estado = when {
                        uiState.analizandoINE -> EstadoPaso.CARGANDO
                        uiState.fotoTomada -> EstadoPaso.COMPLETADO
                        uiState.errorINE != null -> EstadoPaso.ERROR
                        else -> EstadoPaso.PENDIENTE
                    },
                    mensaje = when {
                        uiState.analizandoINE -> "Analizando tu INE..."
                        uiState.fotoTomada -> "INE verificada correctamente"
                        uiState.errorINE != null -> uiState.errorINE ?: "Error al procesar"
                        else -> "Toma una foto clara de tu credencial de elector"
                    },
                    verdePrincipal = verdePrincipal,
                    textoSecundario = textoSecundario,
                    textoPrimario = textoPrimario,
                    errorColor = errorColor,
                    isVisible = showContent,
                    index = 0
                ) {
                    when {
                        uiState.analizandoINE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = verdePrincipal,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Procesando imagen...",
                                    color = textoSecundario,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        else -> {
                            Button(
                                onClick = {
                                    if (tieneCameraPermiso) {
                                        mostrarCamara = true
                                    } else {
                                        cameraPermisoLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.errorINE != null)
                                        errorColor.copy(alpha = 0.2f)
                                    else
                                        verdePrincipal,
                                    contentColor = if (uiState.errorINE != null) errorColor else Color.Black
                                ),
                                border = if (uiState.errorINE != null)
                                    BorderStroke(1.dp, errorColor.copy(alpha = 0.3f))
                                else null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (uiState.errorINE != null) "Reintentar foto" else "Tomar foto",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PASO 2 — GPS
                PasoCardMejorado(
                    numero = "2",
                    titulo = "Dirección de Entrega",
                    estado = when {
                        uiState.gpsObtenido -> EstadoPaso.COMPLETADO
                        else -> EstadoPaso.PENDIENTE
                    },
                    mensaje = if (uiState.gpsObtenido)
                        uiState.direccionEntrega
                    else
                        "Obtén tu ubicación actual para la entrega",
                    verdePrincipal = verdePrincipal,
                    textoSecundario = textoSecundario,
                    textoPrimario = textoPrimario,
                    errorColor = errorColor,
                    isVisible = showContent,
                    index = 1
                ) {
                    if (!uiState.gpsObtenido) {
                        Button(
                            onClick = {
                                if (tieneLocationPermiso) {
                                    val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
                                    try {
                                        fusedLocation.lastLocation.addOnSuccessListener { location ->
                                            if (location != null) {
                                                try {
                                                    @Suppress("DEPRECATION")
                                                    val dir = Geocoder(context, Locale.getDefault())
                                                        .getFromLocation(location.latitude, location.longitude, 1)
                                                        ?.firstOrNull()?.getAddressLine(0)
                                                        ?: "Lat: ${location.latitude}, Lng: ${location.longitude}"
                                                    viewModel.setDireccion(dir)
                                                } catch (e: Exception) {
                                                    viewModel.setDireccion("Lat: ${location.latitude}, Lng: ${location.longitude}")
                                                }
                                            } else {
                                                viewModel.setDireccion("Ubicación no disponible")
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        viewModel.setDireccion("Sin permiso de ubicación")
                                    }
                                } else {
                                    locationPermisoLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = verdePrincipal,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Obtener ubicación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = successColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ubicación confirmada",
                                color = successColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PASO 3 — Identidad
                PasoCardMejorado(
                    numero = "3",
                    titulo = "Confirmar Identidad",
                    estado = when {
                        uiState.nombreValidado -> EstadoPaso.COMPLETADO
                        uiState.fotoTomada -> EstadoPaso.CARGANDO
                        else -> EstadoPaso.PENDIENTE
                    },
                    mensaje = when {
                        uiState.nombreValidado -> "Identidad confirmada - Coincidencia con INE"
                        uiState.fotoTomada -> "Validando identidad..."
                        else -> "Completa el paso 1 para verificar"
                    },
                    verdePrincipal = verdePrincipal,
                    textoSecundario = textoSecundario,
                    textoPrimario = textoPrimario,
                    errorColor = errorColor,
                    isVisible = showContent,
                    index = 2
                ) {
                    // Sin contenido interactivo
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje de error general
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = errorClaro.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = errorColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Botón de compra
                val todosCompletos = uiState.fotoTomada && uiState.gpsObtenido && uiState.nombreValidado

                AnimatedContent(
                    targetState = uiState.isLoading,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "button_state"
                ) { isLoading ->
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = verdePrincipal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.terminarCompra(eventoId, nombreEvento, fecha, hora, precio)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = todosCompletos,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (todosCompletos) verdePrincipal else negroSuperficie,
                                contentColor = if (todosCompletos) Color.Black else textoSecundario,
                                disabledContainerColor = negroSuperficie,
                                disabledContentColor = textoSecundario.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (todosCompletos) "Terminar Compra" else "Completa todos los pasos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón cancelar
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
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Enum para estados de paso
enum class EstadoPaso {
    PENDIENTE,
    CARGANDO,
    COMPLETADO,
    ERROR
}

@Composable
fun PasoCardMejorado(
    numero: String,
    titulo: String,
    estado: EstadoPaso,
    mensaje: String,
    verdePrincipal: Color,
    textoSecundario: Color,
    textoPrimario: Color,
    errorColor: Color,
    isVisible: Boolean,
    index: Int,
    contenido: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }

    val cardColors = when (estado) {
        EstadoPaso.COMPLETADO -> verdePrincipal.copy(alpha = 0.1f)
        EstadoPaso.ERROR -> errorColor.copy(alpha = 0.1f)
        else -> Color(0xFF111111)
    }

    val borderColor = when (estado) {
        EstadoPaso.COMPLETADO -> verdePrincipal.copy(alpha = 0.3f)
        EstadoPaso.ERROR -> errorColor.copy(alpha = 0.3f)
        else -> verdePrincipal.copy(alpha = 0.1f)
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
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = when (estado) {
                        EstadoPaso.COMPLETADO -> verdePrincipal.copy(alpha = 0.2f)
                        EstadoPaso.ERROR -> errorColor.copy(alpha = 0.2f)
                        else -> verdePrincipal.copy(alpha = 0.1f)
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111111).copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header del paso
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Indicador de paso
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (estado) {
                                    EstadoPaso.COMPLETADO -> verdePrincipal
                                    EstadoPaso.ERROR -> errorColor.copy(alpha = 0.2f)
                                    EstadoPaso.CARGANDO -> verdePrincipal.copy(alpha = 0.2f)
                                    else -> verdePrincipal.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (estado) {
                            EstadoPaso.COMPLETADO -> {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            EstadoPaso.CARGANDO -> {
                                CircularProgressIndicator(
                                    color = verdePrincipal,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            else -> {
                                Text(
                                    text = numero,
                                    color = if (estado == EstadoPaso.ERROR) errorColor else verdePrincipal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Título
                    Text(
                        text = titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (estado) {
                            EstadoPaso.COMPLETADO -> verdePrincipal
                            EstadoPaso.ERROR -> errorColor
                            else -> textoPrimario
                        }
                    )
                }

                // Mensaje
                if (mensaje.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = mensaje,
                        fontSize = 13.sp,
                        color = when (estado) {
                            EstadoPaso.COMPLETADO -> verdePrincipal
                            EstadoPaso.ERROR -> errorColor
                            else -> textoSecundario
                        },
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Contenido adicional
                if (estado != EstadoPaso.COMPLETADO) {
                    Spacer(modifier = Modifier.height(16.dp))
                    contenido()
                }
            }
        }
    }
}