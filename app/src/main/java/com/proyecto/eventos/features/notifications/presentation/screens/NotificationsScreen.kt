package com.proyecto.eventos.features.notifications.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
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
import androidx.navigation.NavController
import com.proyecto.eventos.features.notifications.data.local.NotificationEntity
import com.proyecto.eventos.features.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notificaciones by viewModel.notificaciones.collectAsState()
    val noLeidas by viewModel.noLeidasCount.collectAsState()
    
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    val colorScheme = MaterialTheme.colorScheme

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
        // Luz ambiental
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(colorScheme.primary.copy(alpha = 0.05f), Color.Transparent),
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
            // Header con acciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = colorScheme.primary.copy(alpha = 0.2f)),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Centro de Mensajes", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.primary)
                        Text(
                            text = if(noLeidas > 0) "Tienes $noLeidas mensajes nuevos" else "Estás al día",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (notificaciones.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.limpiarTodas() },
                            modifier = Modifier.clip(CircleShape).background(colorScheme.error.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.DeleteSweep, null, tint = colorScheme.error.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            if (notificaciones.isEmpty()) {
                EmptyNotifications()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(notificaciones, key = { it.id }) { notif ->
                        NotificationItem(
                            notif = notif,
                            isVisible = showContent,
                            index = notificaciones.indexOf(notif),
                            onClick = { viewModel.marcarLeida(notif.id) }
                        )
                    }
                }
            }

            // Botón Regresar
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface, 
                    contentColor = colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Panel", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NotificationItem(
    notif: NotificationEntity,
    isVisible: Boolean,
    index: Int,
    onClick: () -> Unit
) {
    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))
    val colorScheme = MaterialTheme.colorScheme
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(400 + (index * 100)))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (notif.leida) colorScheme.surfaceVariant.copy(alpha = 0.4f) else colorScheme.primary.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = if (notif.leida) 1.dp else 1.5.dp,
                color = if (notif.leida) colorScheme.onSurface.copy(alpha = 0.1f) else colorScheme.primary.copy(alpha = 0.4f)
            )
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                // Icono con estado
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (notif.leida) colorScheme.onSurface.copy(alpha = 0.1f) else colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (notif.titulo.contains("Compra", true)) Icons.Default.ConfirmationNumber else Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (notif.leida) colorScheme.onSurfaceVariant else colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            notif.titulo, 
                            fontWeight = FontWeight.Bold, 
                            color = if(notif.leida) colorScheme.onSurface.copy(alpha = 0.7f) else colorScheme.primary, 
                            fontSize = 15.sp
                        )
                        if (!notif.leida) {
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colorScheme.primary))
                        }
                    }
                    Text(
                        notif.cuerpo, 
                        color = if(notif.leida) colorScheme.onSurfaceVariant else colorScheme.onSurface, 
                        fontSize = 13.sp, 
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(date, color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun EmptyNotifications() {
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.NotificationsNone, null, modifier = Modifier.size(80.dp), tint = colorScheme.primary.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))
            Text("Bandeja vacía", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text("No tienes nuevas notificaciones por el momento", textAlign = TextAlign.Center, color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}
