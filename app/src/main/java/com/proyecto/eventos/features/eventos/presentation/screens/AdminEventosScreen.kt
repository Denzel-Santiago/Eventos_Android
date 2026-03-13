package com.proyecto.eventos.features.eventos.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad
import com.proyecto.eventos.features.eventos.presentation.viewmodel.AdminEventosViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdminEventosScreen(
    navController: NavController,
    viewModel: AdminEventosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
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
    val errorClaro = Color(0xFFFEE2E2)

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "admin_animations")
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Gestión de Eventos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdePrincipal
                        )
                        Text(
                            text = "Administra tu catálogo",
                            fontSize = 11.sp,
                            color = textoSecundario
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
                                        verdePrincipal.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = verdePrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = negroSuperficie
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.abrirDialogoNuevo() },
                containerColor = verdePrincipal,
                contentColor = Color.Black,
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = verdePrincipal.copy(alpha = 0.5f)
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nuevo evento",
                    modifier = Modifier.size(24.dp)
                )
            }
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
                    .padding(paddingValues)
            ) {
                // Header con estadísticas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        EstadisticaItem(
                            icono = Icons.Default.Event,
                            valor = eventos.size.toString(),
                            etiqueta = "Total",
                            color = verdePrincipal
                        )

                        EstadisticaItem(
                            icono = Icons.Default.Inventory,
                            valor = eventos.sumOf { it.stock }.toString(),
                            etiqueta = "Boletos",
                            color = Color(0xFFFFA500)
                        )

                        EstadisticaItem(
                            icono = Icons.Default.AttachMoney,
                            valor = eventos.size.toString(),
                            etiqueta = "Activos",
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }

                if (uiState.isLoading && eventos.isEmpty()) {
                    // Pantalla de carga
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
                                        imageVector = Icons.Default.EventNote,
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
                        }
                    }
                } else if (eventos.isEmpty()) {
                    // Estado vacío
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
                                text = "No hay eventos creados",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = textoPrimario
                            )
                            Text(
                                text = "Presiona el botón + para crear tu primer evento",
                                fontSize = 14.sp,
                                color = textoSecundario,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    // Lista de eventos
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = eventos,
                            key = { it.id }
                        ) { evento ->
                            EventoAdminCard(
                                evento = evento,
                                isVisible = showContent,
                                index = eventos.indexOf(evento),
                                verdePrincipal = verdePrincipal,
                                textoSecundario = textoSecundario,
                                textoPrimario = textoPrimario,
                                errorColor = errorColor,
                                onEditar = { viewModel.abrirDialogoEditar(evento) },
                                onEliminar = { viewModel.eliminarEvento(evento.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de formulario mejorado
    if (uiState.mostrarDialog) {
        FormularioEventoDialog(
            uiState = uiState,
            verdePrincipal = verdePrincipal,
            textoSecundario = textoSecundario,
            textoPrimario = textoPrimario,
            errorColor = errorColor,
            errorClaro = errorClaro,
            onNombreChange = viewModel::onNombreChange,
            onFechaChange = viewModel::onFechaChange,
            onHoraChange = viewModel::onHoraChange,
            onUbicacionChange = viewModel::onUbicacionChange,
            onPrecioChange = viewModel::onPrecioChange,
            onStockChange = viewModel::onStockChange,
            onImagenSeleccionada = viewModel::onImagenSeleccionada,
            onGuardar = viewModel::guardarEvento,
            onCancelar = viewModel::cerrarDialogo
        )
    }
}

@Composable
fun EstadisticaItem(
    icono: ImageVector,
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
            color = Color.White
        )
        Text(
            text = etiqueta,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEventoDialog(
    uiState: AdminEventosViewModel.AdminEventosUiState,
    verdePrincipal: Color,
    textoSecundario: Color,
    textoPrimario: Color,
    errorColor: Color,
    errorClaro: Color,
    onNombreChange: (String) -> Unit,
    onFechaChange: (String) -> Unit,
    onHoraChange: (String) -> Unit,
    onUbicacionChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onImagenSeleccionada: (Uri) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Estados para los diálogos de fecha y hora
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Estados para validación
    var nombreValid by remember { mutableStateOf(true) }
    var fechaValid by remember { mutableStateOf(true) }
    var horaValid by remember { mutableStateOf(true) }
    var ubicacionValid by remember { mutableStateOf(true) }
    var precioValid by remember { mutableStateOf(true) }
    var stockValid by remember { mutableStateOf(true) }

    // Focus requesters para navegación entre campos
    val (nombreFocus, fechaFocus, horaFocus, ubicacionFocus, precioFocus, stockFocus) = FocusRequester.createRefs()

    // Launcher para abrir la galería
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagenSeleccionada(it) }
    }

    // Validaciones en tiempo real
    LaunchedEffect(uiState.dialogNombre) {
        nombreValid = uiState.dialogNombre.length >= 3 || uiState.dialogNombre.isEmpty()
    }
    LaunchedEffect(uiState.dialogFecha) {
        fechaValid = uiState.dialogFecha.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) || uiState.dialogFecha.isEmpty()
    }
    LaunchedEffect(uiState.dialogHora) {
        horaValid = uiState.dialogHora.matches(Regex("^\\d{2}:\\d{2}$")) || uiState.dialogHora.isEmpty()
    }
    LaunchedEffect(uiState.dialogUbicacion) {
        ubicacionValid = uiState.dialogUbicacion.length >= 3 || uiState.dialogUbicacion.isEmpty()
    }
    LaunchedEffect(uiState.dialogPrecio) {
        precioValid = uiState.dialogPrecio.toDoubleOrNull() != null || uiState.dialogPrecio.isEmpty()
    }
    LaunchedEffect(uiState.dialogStock) {
        stockValid = uiState.dialogStock.toIntOrNull() != null || uiState.dialogStock.isEmpty()
    }

    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = verdePrincipal.copy(alpha = 0.3f)
                ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header del diálogo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    verdePrincipal.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = if (uiState.eventoEditando == null) "Crear Nuevo Evento" else "Editar Evento",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdePrincipal
                        )
                        Text(
                            text = if (uiState.eventoEditando == null)
                                "Completa los datos del evento"
                            else
                                "Modifica los datos del evento",
                            fontSize = 13.sp,
                            color = textoSecundario
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Contenido del formulario
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // ── SELECTOR DE IMAGEN ───────────────────
                    Text(
                        text = "Imagen del evento",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textoSecundario
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var isImagePressed by remember { mutableStateOf(false) }
                    val imageScale by animateFloatAsState(
                        targetValue = if (isImagePressed) 0.98f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .scale(imageScale)
                            .clickable {
                                isImagePressed = true
                                imagePicker.launch("image/*")
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = when {
                                uiState.subiendoImagen -> verdePrincipal
                                uiState.imagenUriPreview != null || uiState.imagenBase64Actual.isNotBlank() -> verdePrincipal
                                else -> textoSecundario.copy(alpha = 0.2f)
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                uiState.subiendoImagen -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            color = verdePrincipal,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Procesando imagen...",
                                            color = textoSecundario,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                uiState.imagenUriPreview != null -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(uiState.imagenUriPreview),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Text("Cambiar imagen", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                                uiState.imagenBase64Actual.isNotBlank() -> {
                                    val bitmap = remember(uiState.imagenBase64Actual) {
                                        try {
                                            val bytes = android.util.Base64.decode(uiState.imagenBase64Actual, android.util.Base64.DEFAULT)
                                            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        } catch (e: Exception) { null }
                                    }
                                    if (bitmap != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(bitmap),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Text("Cambiar imagen", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.AddPhotoAlternate, null, tint = verdePrincipal, modifier = Modifier.size(48.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("Toca para seleccionar una imagen", color = textoSecundario, fontSize = 14.sp)
                                        Text("de tu galería", color = textoSecundario.copy(alpha = 0.5f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // ── CAMPO NOMBRE ──────────────────────────
                    CampoTextoMejorado(
                        label = "Nombre del evento",
                        value = uiState.dialogNombre,
                        onValueChange = onNombreChange,
                        isValid = nombreValid,
                        errorMessage = "Mínimo 3 caracteres",
                        verdePrincipal = verdePrincipal,
                        textoSecundario = textoSecundario,
                        textoPrimario = textoPrimario,
                        errorColor = errorColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { fechaFocus.requestFocus() }),
                        focusRequester = nombreFocus,
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = verdePrincipal, modifier = Modifier.size(16.dp)) }
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── CAMPOS DE FECHA Y HORA CON SELECTORES ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Fecha
                        OutlinedTextField(
                            value = uiState.dialogFecha,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).focusRequester(fechaFocus).clickable { showDatePicker = true },
                            shape = RoundedCornerShape(14.dp),
                            trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.CalendarToday, null, tint = verdePrincipal) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verdePrincipal, unfocusedBorderColor = textoSecundario.copy(alpha = 0.2f),
                                focusedTextColor = textoPrimario, unfocusedTextColor = textoPrimario
                            )
                        )

                        // Hora
                        OutlinedTextField(
                            value = uiState.dialogHora,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).focusRequester(horaFocus).clickable { showTimePicker = true },
                            shape = RoundedCornerShape(14.dp),
                            trailingIcon = { IconButton(onClick = { showTimePicker = true }) { Icon(Icons.Default.AccessTime, null, tint = verdePrincipal) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verdePrincipal, unfocusedBorderColor = textoSecundario.copy(alpha = 0.2f),
                                focusedTextColor = textoPrimario, unfocusedTextColor = textoPrimario
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── CAMPO UBICACIÓN ───────────────────────
                    CampoTextoMejorado(
                        label = "Ubicación",
                        value = uiState.dialogUbicacion,
                        onValueChange = onUbicacionChange,
                        isValid = ubicacionValid,
                        errorMessage = "Mínimo 3 caracteres",
                        verdePrincipal = verdePrincipal,
                        textoSecundario = textoSecundario,
                        textoPrimario = textoPrimario,
                        errorColor = errorColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { precioFocus.requestFocus() }),
                        focusRequester = ubicacionFocus,
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = verdePrincipal, modifier = Modifier.size(16.dp)) }
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── CAMPOS DE PRECIO Y STOCK ──────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CampoTextoMejorado(
                            label = "Precio (MXN)",
                            value = uiState.dialogPrecio,
                            onValueChange = onPrecioChange,
                            isValid = precioValid,
                            errorMessage = "Debe ser un número",
                            verdePrincipal = verdePrincipal,
                            textoSecundario = textoSecundario,
                            textoPrimario = textoPrimario,
                            errorColor = errorColor,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { stockFocus.requestFocus() }),
                            focusRequester = precioFocus,
                            leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = verdePrincipal, modifier = Modifier.size(16.dp)) }
                        )

                        CampoTextoMejorado(
                            label = "Stock",
                            value = uiState.dialogStock,
                            onValueChange = onStockChange,
                            isValid = stockValid,
                            errorMessage = "Debe ser entero",
                            verdePrincipal = verdePrincipal,
                            textoSecundario = textoSecundario,
                            textoPrimario = textoPrimario,
                            errorColor = errorColor,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            focusRequester = stockFocus,
                            leadingIcon = { Icon(Icons.Default.Inventory, null, tint = verdePrincipal, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    // Mensaje de error general
                    AnimatedVisibility(visible = uiState.error != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = errorClaro.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = uiState.error ?: "", color = errorColor, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // ── BOTONES ───────────────────────────────
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { focusManager.clearFocus(); onCancelar() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textoSecundario),
                            border = BorderStroke(1.dp, verdePrincipal.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Cancelar") }

                        Button(
                            onClick = { focusManager.clearFocus(); onGuardar() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            enabled = !uiState.isLoading && !uiState.subiendoImagen && nombreValid && fechaValid && horaValid && ubicacionValid && precioValid && stockValid && uiState.dialogNombre.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                            else Text("Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // DATE PICKER
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        onFechaChange(formatter.format(Date(it)))
                    }
                    showDatePicker = false
                }) { Text("OK", color = verdePrincipal) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = textoSecundario) } }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text("Selecciona la fecha", color = verdePrincipal, modifier = Modifier.padding(16.dp)) },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1A1A1A), selectedDayContainerColor = verdePrincipal,
                    selectedDayContentColor = Color.Black, todayContentColor = verdePrincipal,
                    dayContentColor = Color.White, weekdayContentColor = textoSecundario
                )
            )
        }
    }

    // TIME PICKER
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Selecciona la hora", color = verdePrincipal, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFF0A0A0A), selectorColor = verdePrincipal,
                            clockDialSelectedContentColor = Color.Black, clockDialUnselectedContentColor = Color.White
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancelar", color = textoSecundario) }
                        TextButton(onClick = {
                            val h = timePickerState.hour.toString().padStart(2, '0')
                            val m = timePickerState.minute.toString().padStart(2, '0')
                            onHoraChange("$h:$m")
                            showTimePicker = false
                        }) { Text("OK", color = verdePrincipal) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoMejorado(
    label: String, value: String, onValueChange: (String) -> Unit,
    isValid: Boolean, errorMessage: String, verdePrincipal: Color,
    textoSecundario: Color, textoPrimario: Color, errorColor: Color,
    modifier: Modifier = Modifier, keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default, focusRequester: FocusRequester? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth().then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = verdePrincipal, unfocusedBorderColor = textoSecundario.copy(alpha = 0.2f),
            focusedTextColor = textoPrimario, unfocusedTextColor = textoPrimario, errorBorderColor = errorColor
        ),
        keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        isError = value.isNotEmpty() && !isValid,
        supportingText = { if (value.isNotEmpty() && !isValid) Text(errorMessage, color = errorColor, fontSize = 11.sp) },
        leadingIcon = leadingIcon, singleLine = true
    )
}

@Composable
fun EventoAdminCard(
    evento: EventoEntidad, isVisible: Boolean, index: Int,
    verdePrincipal: Color, textoSecundario: Color, textoPrimario: Color,
    errorColor: Color, onEditar: () -> Unit, onEliminar: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, label = "card_scale")

    val bitmap = remember(evento.imagen) {
        if (evento.imagen.isNotBlank()) {
            try {
                val bytes = android.util.Base64.decode(evento.imagen, android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(400 + (index * 50))) + fadeIn()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().scale(scale).shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF2A2A2A))) {
                    if (bitmap != null) {
                        Image(painter = rememberAsyncImagePainter(bitmap), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.Image, null, tint = verdePrincipal.copy(alpha = 0.3f), modifier = Modifier.align(Alignment.Center))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(evento.nombre, color = textoPrimario, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Text("${evento.fecha} • ${evento.hora}", color = textoSecundario, fontSize = 13.sp)
                    Text(evento.ubicacion, color = textoSecundario.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1)
                }
                IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, "Editar", tint = verdePrincipal) }
                IconButton(onClick = onEliminar) { Icon(Icons.Default.Delete, "Eliminar", tint = errorColor) }
            }
        }
    }
}
