//com.proyecto.eventos.features.compras.presentation.screens.VerificacionIdentidadScreen

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.proyecto.eventos.features.compras.presentation.viewmodel.ComprasViewModel
import java.io.File
import java.util.Locale
import java.util.concurrent.Executors

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

    // Estado cámara
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }
    val notificacionPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tieneNotificacionPermiso = granted }

    // Pedir permiso de notificación al entrar a la pantalla
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!tieneNotificacionPermiso) {
                notificacionPermisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Navegar si compra exitosa
    LaunchedEffect(uiState.compraExitosa) {
        if (uiState.compraExitosa) {
            navController.navigate("panel") {
                popUpTo("panel") { inclusive = false }
            }
        }
    }

    if (mostrarCamara) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            val cameraProviderFuture = remember {
                ProcessCameraProvider.getInstance(context)
            }
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = Executors.newSingleThreadExecutor()
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val capture = ImageCapture.Builder().build()
                        imageCapture = capture
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, executor)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = {
                    val file = File(
                        context.filesDir,
                        "ines/${System.currentTimeMillis()}.jpg"
                    ).also { it.parentFile?.mkdirs() }

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    imageCapture?.takePicture(
                        outputOptions,
                        Executors.newSingleThreadExecutor(),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                viewModel.setFotoPath(file.absolutePath)
                                mostrarCamara = false
                            }
                            override fun onError(exception: ImageCaptureException) {
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
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Tomar foto",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { mostrarCamara = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
            }
        }
    } else {
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

            // PASO 1 — Foto INE
            PasoCard(
                numero = "1",
                titulo = "Foto de INE",
                descripcion = if (uiState.fotoTomada)
                    "✅ Foto capturada correctamente"
                else
                    "Toma una foto de tu identificación oficial",
                completado = uiState.fotoTomada,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
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
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir Cámara", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PASO 2 — GPS
            PasoCard(
                numero = "2",
                titulo = "Dirección de Entrega",
                descripcion = if (uiState.gpsObtenido)
                    "✅ ${uiState.direccionEntrega}"
                else
                    "Obtén tu ubicación actual para la entrega",
                completado = uiState.gpsObtenido,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
                Button(
                    onClick = {
                        if (tieneLocationPermiso) {
                            val fusedLocation = LocationServices
                                .getFusedLocationProviderClient(context)
                            try {
                                fusedLocation.lastLocation.addOnSuccessListener { location ->
                                    if (location != null) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            @Suppress("DEPRECATION")
                                            val addresses = geocoder.getFromLocation(
                                                location.latitude,
                                                location.longitude,
                                                1
                                            )
                                            val direccion = addresses?.firstOrNull()
                                                ?.getAddressLine(0)
                                                ?: "Lat: ${location.latitude}, Lng: ${location.longitude}"
                                            viewModel.setDireccion(direccion)
                                        } catch (e: Exception) {
                                            viewModel.setDireccion(
                                                "Lat: ${location.latitude}, Lng: ${location.longitude}"
                                            )
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
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Ubicación GPS", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PASO 3 — Validar nombre
            PasoCard(
                numero = "3",
                titulo = "Confirmar Identidad",
                descripcion = if (uiState.nombreValidado)
                    "✅ Identidad verificada"
                else
                    "Ingresa tu nombre completo tal como aparece en tu cuenta",
                completado = uiState.nombreValidado,
                verdePrincipal = VerdePrincipal,
                textoSecundario = TextoSecundario
            ) {
                OutlinedTextField(
                    value = uiState.nombreIngresado,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Tu nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePrincipal,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = VerdePrincipal,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextoSecundario,
                        unfocusedTextColor = TextoSecundario
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.validarNombre() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.nombreIngresado.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Validar Nombre", fontWeight = FontWeight.Bold)
                }
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
                        viewModel.terminarCompra(
                            eventoId = eventoId,
                            nombreEvento = nombreEvento,
                            fecha = fecha,
                            hora = hora,
                            precio = precio
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = todosCompletos,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (todosCompletos) VerdePrincipal else Color.Gray,
                        contentColor = Color.Black
                    )
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
    descripcion: String,
    completado: Boolean,
    verdePrincipal: Color,
    textoSecundario: Color,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111111)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (completado)
                                verdePrincipal
                            else
                                verdePrincipal.copy(alpha = 0.2f),
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descripcion,
                fontSize = 13.sp,
                color = textoSecundario.copy(alpha = 0.7f)
            )
            if (!completado) {
                Spacer(modifier = Modifier.height(12.dp))
                contenido()
            }
        }
    }
}