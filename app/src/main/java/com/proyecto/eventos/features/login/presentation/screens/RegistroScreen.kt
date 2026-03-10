// features/auth/presentation/screens/RegistroScreen.kt
package com.proyecto.eventos.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.proyecto.eventos.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registroState.collectAsStateWithLifecycle()

    // Efecto para navegar cuando el registro es exitoso
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
            viewModel.resetRegistroState()
        }
    }

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo o título
        Text(
            text = "Sweeper Tickets",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Tarjeta de registro
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111111)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo Nombre completo
                OutlinedTextField(
                    value = state.nombreCompleto,
                    onValueChange = { viewModel.onRegistroNombreChange(it) },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = state.errorMessage != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Email
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onRegistroEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = state.errorMessage != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onRegistroPasswordChange(it) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors(),
                    isError = state.errorMessage != null
                )

                // Mensaje de error
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Registrarse
                Button(
                    onClick = { viewModel.registro() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.Black
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarse")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Ya tengo cuenta
                TextButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = VerdeHover
                    )
                }

                // Botón Regresar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextoSecundario
                    )
                ) {
                    Text("Regresar")
                }
            }
        }
    }
}