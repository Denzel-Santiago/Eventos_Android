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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.proyecto.eventos.features.compras.presentation.viewmodel.ComprasViewModel
import java.io.File
import java.util.Locale

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

    val NegroFondo = Color(0xFF0A0A0A)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val TextoSecundario = Color(0xFFE5E7EB)

    var mostrarCamara by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Permiso cámara
    var tieneCameraPermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tieneCameraPermiso = granted }

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

    // ── Diálogo de confirmación de texto OCR ──────────────────────────────
    if (uiState.mostrandoConfirmacion) {
        Dialog(onDismissRequest = { viewModel.rechazarINE() }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "¿Es tu INE?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdePrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Texto detectado en la imagen:",
                        fontSize = 13.sp,
                        color = TextoSecundario.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Caja con el texto OCR detectado
                    Surface(
                        color = Color(0xFF0D0D0D),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.textoDetectado.take(600), // máx 600 chars
                            fontSize = 12.sp,
                            color = TextoSecundario,
                            modifier = Modifier.padding(12.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicador de nombre encontrado
                    if (uiState.nombreEncontradoEnINE) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VerdePrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tu nombre fue encontrado en la INE ✅",
                                color = VerdePrincipal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nombre no detectado automáticamente",
                                color = Color(0xFFFFA500),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "¿El texto corresponde a tu credencial de elector?",
                        fontSize = 14.sp,
                        color = TextoSecundario,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.rechazarINE() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text("No, retomar")
                        }
                        Button(
                            onClick = { viewModel.confirmarINE() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdePrincipal,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Sí, confirmar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── Vista de cámara ───────────────────────────────────────────────────
    if (mostrarCamara) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

            DisposableEffect(lifecycleOwner) {
                onDispose {
                    try { cameraProviderFuture.get()?.unbindAll() } catch (e: Exception) { }
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
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )

            // Instrucción al usuario
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "📸 Enfoca tu INE completa y toma la foto",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
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
                    .size(80.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdePrincipal,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(32.dp))
            }

            IconButton(
                onClick = {
                    try { cameraProviderFuture.get()?.unbindAll() } catch (e: Exception) { }
                    mostrarCamara = false
                },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
            }
        }

    } else {
        // ── Vista principal ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NegroFondo)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Verificación de Identidad",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal
            )
            Text(
                text = nombreEvento,
                fontSize = 16.sp,
                color = TextoSecundario.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "$fecha  $hora  •  $$precio",
                fontSize = 14.sp,
                color = TextoSecundario.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PASO 1 — Foto INE con OCR
            PasoCard(
                numero = "1",
                titulo = "Foto de INE",
                descripcion = when {
                    uiState.analizandoINE -> "⏳ Leyendo texto de tu INE..."
                    uiState.fotoTomada    -> "✅ INE verificada correctamente"
                    uiState.errorINE != null -> uiState.errorINE
                    else -> "Toma una foto clara de tu credencial de elector mexicana"
                },
                completado = uiState.fotoTomada,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
                if (uiState.analizandoINE) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = VerdePrincipal,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Analizando imagen...", color = TextoSecundario, fontSize = 13.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (tieneCameraPermiso) mostrarCamara = true
                            else cameraPermisoLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.errorINE != null) Color(0xFF8B0000) else VerdePrincipal,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (uiState.errorINE != null) "Reintentar foto" else "Abrir Cámara",
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.errorINE != null) Color.White else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PASO 2 — GPS
            PasoCard(
                numero = "2",
                titulo = "Dirección de Entrega",
                descripcion = if (uiState.gpsObtenido) "✅ ${uiState.direccionEntrega}"
                              else "Obtén tu ubicación actual para la entrega",
                completado = uiState.gpsObtenido,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Ubicación GPS", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PASO 3 — Identidad (se completa automáticamente con la INE)
            PasoCard(
                numero = "3",
                titulo = "Confirmar Identidad",
                descripcion = when {
                    uiState.nombreValidado -> "✅ Identidad confirmada — nombre verificado con la INE"
                    uiState.fotoTomada     -> "✅ En espera de completar pasos anteriores"
                    else                   -> "Se completará automáticamente al verificar tu INE"
                },
                completado = uiState.nombreValidado,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
                // Sin contenido interactivo — se completa al confirmar la INE
            }

            Spacer(modifier = Modifier.height(32.dp))

            uiState.error?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val todosCompletos = uiState.fotoTomada && uiState.gpsObtenido && uiState.nombreValidado

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdePrincipal)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.terminarCompra(eventoId, nombreEvento, fecha, hora, precio)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = todosCompletos,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (todosCompletos) VerdePrincipal else Color.Gray,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (todosCompletos) "Terminar Compra" else "Completa todos los pasos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoSecundario)
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun PasoCard(
    numero: String,
    titulo: String,
    descripcion: String?,
    completado: Boolean,
    verdePrincipal: Color,
    textoSecundario: Color,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (completado) verdePrincipal else verdePrincipal.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (completado) "✓" else numero,
                        color = if (completado) Color.Black else verdePrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoSecundario
                )
            }
            if (!descripcion.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = textoSecundario.copy(alpha = 0.7f)
                )
            }
            if (!completado) {
                Spacer(modifier = Modifier.height(12.dp))
                contenido()
            }
        }
    }
}
