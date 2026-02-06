    package com.proyecto.eventos.features.eventos.presentation.screens

    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.proyecto.eventos.core.network.SessionManager
    import com.proyecto.eventos.features.eventos.presentation.components.EventoItem
    import com.proyecto.eventos.features.eventos.presentation.viewmodel.InicioViewModel
    import com.proyecto.eventos.features.eventos.presentation.viewmodel.InicioViewModelFactory

    @Composable
    fun InicioScreen(
        navController: NavController
    ) {

        val estaAutenticado = SessionManager.estaAutenticado()
        val esAdmin = SessionManager.esAdmin()

        // 🎨 Colores del proyecto
        val NegroFondo = Color(0xFF0A0A0A)
        val VerdePrincipal = Color(0xFF2DD4BF)
        val VerdeHover = Color(0xFF14B8A6)
        val TextoSecundario = Color(0xFFE5E7EB)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NegroFondo)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // 🏷️ TÍTULO
            Text(
                text = "Eventos",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Compra tus boletos de forma rápida",
                color = TextoSecundario,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🛒 BOTÓN PRINCIPAL (USUARIO)
            Button(
                onClick = {
                    if (estaAutenticado) {
                        navController.navigate("compra")
                    } else {
                        navController.navigate("login_compra")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdePrincipal,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (estaAutenticado) "Comprar boletos" else "Iniciar sesión",
                    fontWeight = FontWeight.Bold
                )
            }

            // 🔐 BLOQUE ADMIN
            if (esAdmin) {

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = { navController.navigate("admin_eventos") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeHover.copy(alpha = 0.2f),
                            contentColor = VerdeHover
                        )
                    ) {
                        Text("Admin Eventos", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { navController.navigate("admin_usuarios") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeHover.copy(alpha = 0.2f),
                            contentColor = VerdeHover
                        )
                    ) {
                        Text("Admin Usuarios", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 🚪 CERRAR SESIÓN
            if (estaAutenticado) {
                OutlinedButton(
                    onClick = {
                        SessionManager.cerrarSesion()
                        navController.navigate("inicio") {
                            popUpTo("inicio") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
