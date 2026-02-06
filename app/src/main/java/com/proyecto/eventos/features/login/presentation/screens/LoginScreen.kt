//features/login/presentation/screens/LoginScreen.kt
package com.proyecto.eventos.features.login.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.core.network.SessionManager
import com.proyecto.eventos.features.login.presentation.viewmodel.LoginViewModel
import com.proyecto.eventos.features.login.presentation.viewmodel.LoginViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
    redirectToCompra: Boolean = false,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory())
) {
    val NegroFondo = Color(0xFF0A0A0A)
    val NegroContenedor = Color(0xFF111111)
    val VerdePrincipal = Color(0xFF2DD4BF)
    val VerdeHover = Color(0xFF14B8A6)
    val TextoSecundario = Color(0xFFE5E7EB)
    val Blanco10 = Color.White.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroFondo)
            .statusBarsPadding() // ✅ no invade la barra
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Iniciar Sesión",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Usuario
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = darkTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Contraseña
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = darkTextFieldColors()
        )

        // 🔴 Error
        viewModel.mensajeError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 INGRESAR
        Button(
            onClick = {
                viewModel.login {

                    if (SessionManager.esAdmin()) {
                        // 🔐 Admin → Inicio
                        navController.navigate("inicio") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // 👤 Usuario → Compra
                        navController.navigate("compra") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal,
                contentColor = Color.Black
            )
        ) {
            Text("Ingresar")
        }


        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 REGISTRO
        TextButton(
            onClick = { navController.navigate("registro") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Regístrate", color = VerdeHover)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 REGRESAR
        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextoSecundario
            )
        ) {
            Text("Regresar")
        }
    }
}@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF2DD4BF),
        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
        focusedLabelColor = Color(0xFF2DD4BF),
        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
        focusedTextColor = Color(0xFFE5E7EB),
        unfocusedTextColor = Color(0xFFE5E7EB),
        cursorColor = Color(0xFF2DD4BF)
    )



