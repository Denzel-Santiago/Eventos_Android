//features/login/presentation/screens/RegistroScreen.kt
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
import com.proyecto.eventos.features.login.presentation.viewmodel.RegistroViewModel
import com.proyecto.eventos.features.login.presentation.viewmodel.RegistroViewModelFactory

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory())
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
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Crear Cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = darkTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = darkTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = darkTextFieldColors()
        )

        viewModel.mensajeError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.registro {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal,
                contentColor = Color.Black
            )
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextoSecundario
            )
        ) {
            Text("Ya tengo cuenta")
        }
    }
}
