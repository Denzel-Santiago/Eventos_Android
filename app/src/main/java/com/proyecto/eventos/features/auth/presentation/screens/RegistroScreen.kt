package com.proyecto.eventos.features.auth.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.auth.presentation.viewmodels.RegistroViewModel
// IMPORTANTE: Importar los colores del tema
import com.proyecto.eventos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    onRegisterSuccess: () -> Unit,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Estados para validaciones visuales
    var nombreValid by remember { mutableStateOf(true) }
    var emailValid by remember { mutableStateOf(true) }
    var passwordValid by remember { mutableStateOf(true) }
    var confirmPasswordValid by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordStrength by remember { mutableStateOf(0) } // 0-3 para medir fortaleza

    // Focus requesters para navegación entre campos
    val (nombreFocus, emailFocus, passwordFocus, confirmPasswordFocus) = FocusRequester.createRefs()

    // Animación de entrada
    val enterTransition = remember {
        slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400))
    }

    // Calcular fortaleza de contraseña
    fun calculatePasswordStrength(password: String): Int {
        return when {
            password.length < 6 -> 0
            password.length < 8 -> 1
            password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).+$")) -> 3
            password.matches(Regex("^(?=.*[A-Z])(?=.*[a-z]).+$")) -> 2
            else -> 1
        }
    }

    // Efecto para navegar cuando el registro es exitoso
    LaunchedEffect(viewModel.isSuccess.value) {
        if (viewModel.isSuccess.value) {
            onRegisterSuccess()
        }
    }

    // Actualizar fortaleza de contraseña
    LaunchedEffect(viewModel.password.value) {
        passwordStrength = calculatePasswordStrength(viewModel.password.value)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NegroFondo, NegroSuperficie),
                    startY = 0f,
                    endY = screenHeight.value * 0.3f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Espacio superior flexible
            Spacer(modifier = Modifier.weight(0.2f))

            // Animación de entrada para el título
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icono decorativo
                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = VerdePrincipal.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = VerdePrincipal,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Crear Cuenta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdePrincipal,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Únete a Sweeper Tickets",
                        fontSize = 16.sp,
                        color = TextoSecundario,
                        letterSpacing = 0.25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre - CORREGIDO
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                OutlinedTextField(
                    value = viewModel.nombre.value,
                    onValueChange = {
                        viewModel.onNombreChange(it)
                        nombreValid = it.length >= 3 || it.isEmpty()
                    },
                    label = { Text("Nombre completo") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (nombreValid && viewModel.nombre.value.isNotEmpty())
                                VerdePrincipal else TextoSecundario
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .focusRequester(nombreFocus),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePrincipal,
                        unfocusedBorderColor = TextoSecundario.copy(alpha = 0.3f),
                        focusedLabelColor = VerdePrincipal,
                        unfocusedLabelColor = TextoSecundario,
                        // CORREGIDO: Texto siempre blanco
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = VerdePrincipal,
                        unfocusedLeadingIconColor = TextoSecundario,
                        cursorColor = VerdePrincipal,
                        // CORREGIDO: Colores para estado de error
                        errorTextColor = Color.White,
                        errorBorderColor = ErrorColor,
                        errorLabelColor = ErrorColor,
                        errorLeadingIconColor = ErrorColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { emailFocus.requestFocus() }
                    ),
                    isError = viewModel.nombre.value.isNotEmpty() && !nombreValid,
                    supportingText = {
                        if (viewModel.nombre.value.isNotEmpty() && !nombreValid) {
                            Text(
                                text = "Mínimo 3 caracteres",
                                color = ErrorColor,
                                fontSize = 12.sp
                            )
                        }
                    },
                    // CORREGIDO: Forzar color blanco
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email - CORREGIDO
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                OutlinedTextField(
                    value = viewModel.email.value,
                    onValueChange = {
                        viewModel.onEmailChange(it)
                        emailValid = it.contains("@") && it.contains(".")
                    },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = if (emailValid && viewModel.email.value.isNotEmpty())
                                VerdePrincipal else TextoSecundario
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .focusRequester(emailFocus),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePrincipal,
                        unfocusedBorderColor = TextoSecundario.copy(alpha = 0.3f),
                        focusedLabelColor = VerdePrincipal,
                        unfocusedLabelColor = TextoSecundario,
                        // CORREGIDO: Texto siempre blanco
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = VerdePrincipal,
                        unfocusedLeadingIconColor = TextoSecundario,
                        cursorColor = VerdePrincipal,
                        // CORREGIDO: Colores para estado de error
                        errorTextColor = Color.White,
                        errorBorderColor = ErrorColor,
                        errorLabelColor = ErrorColor,
                        errorLeadingIconColor = ErrorColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocus.requestFocus() }
                    ),
                    isError = viewModel.email.value.isNotEmpty() && !emailValid,
                    supportingText = {
                        if (viewModel.email.value.isNotEmpty() && !emailValid) {
                            Text(
                                text = "Email inválido",
                                color = ErrorColor,
                                fontSize = 12.sp
                            )
                        }
                    },
                    // CORREGIDO: Forzar color blanco
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña con indicador de fortaleza - CORREGIDO
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                Column {
                    OutlinedTextField(
                        value = viewModel.password.value,
                        onValueChange = {
                            viewModel.onPasswordChange(it)
                            passwordValid = it.length >= 6
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (passwordValid && viewModel.password.value.isNotEmpty())
                                    VerdePrincipal else TextoSecundario
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = VerdePrincipal
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .focusRequester(passwordFocus),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdePrincipal,
                            unfocusedBorderColor = TextoSecundario.copy(alpha = 0.3f),
                            focusedLabelColor = VerdePrincipal,
                            unfocusedLabelColor = TextoSecundario,
                            // CORREGIDO: Texto siempre blanco
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLeadingIconColor = VerdePrincipal,
                            unfocusedLeadingIconColor = TextoSecundario,
                            cursorColor = VerdePrincipal,
                            // CORREGIDO: Colores para estado de error
                            errorTextColor = Color.White,
                            errorBorderColor = ErrorColor,
                            errorLabelColor = ErrorColor,
                            errorLeadingIconColor = ErrorColor
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { confirmPasswordFocus.requestFocus() }
                        ),
                        isError = viewModel.password.value.isNotEmpty() && !passwordValid,
                        supportingText = {
                            if (viewModel.password.value.isNotEmpty() && !passwordValid) {
                                Text(
                                    text = "Mínimo 6 caracteres",
                                    color = ErrorColor,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        // CORREGIDO: Forzar color blanco
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )

                    // Indicador de fortaleza de contraseña
                    if (viewModel.password.value.isNotEmpty() && passwordValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordStrengthIndicator(strength = passwordStrength)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Contraseña - CORREGIDO
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                OutlinedTextField(
                    value = viewModel.confirmPassword.value,
                    onValueChange = {
                        viewModel.onConfirmPasswordChange(it)
                        confirmPasswordValid = it == viewModel.password.value && it.isNotEmpty()
                    },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (confirmPasswordValid && viewModel.confirmPassword.value.isNotEmpty())
                                VerdePrincipal else TextoSecundario
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible)
                                    Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = VerdePrincipal
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .focusRequester(confirmPasswordFocus),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePrincipal,
                        unfocusedBorderColor = TextoSecundario.copy(alpha = 0.3f),
                        focusedLabelColor = VerdePrincipal,
                        unfocusedLabelColor = TextoSecundario,
                        // CORREGIDO: Texto siempre blanco
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = VerdePrincipal,
                        unfocusedLeadingIconColor = TextoSecundario,
                        cursorColor = VerdePrincipal,
                        // CORREGIDO: Colores para estado de error
                        errorTextColor = Color.White,
                        errorBorderColor = ErrorColor,
                        errorLabelColor = ErrorColor,
                        errorLeadingIconColor = ErrorColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (allFieldsValid(
                                    nombreValid, emailValid, passwordValid, confirmPasswordValid,
                                    viewModel.nombre.value, viewModel.email.value,
                                    viewModel.password.value, viewModel.confirmPassword.value
                                )) {
                                viewModel.registrar()
                            }
                        }
                    ),
                    isError = viewModel.confirmPassword.value.isNotEmpty() && !confirmPasswordValid,
                    supportingText = {
                        if (viewModel.confirmPassword.value.isNotEmpty() && !confirmPasswordValid) {
                            Text(
                                text = "Las contraseñas no coinciden",
                                color = ErrorColor,
                                fontSize = 12.sp
                            )
                        }
                    },
                    // CORREGIDO: Forzar color blanco
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )
            }

            // Mensaje de error general con animación
            AnimatedVisibility(
                visible = viewModel.error.value != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorClaro.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = viewModel.error.value ?: "",
                        color = ErrorColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        color = VerdePrincipal,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.registrar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal,
                            contentColor = Color.Black,
                            disabledContainerColor = VerdePrincipal.copy(alpha = 0.3f),
                            disabledContentColor = Color.Black.copy(alpha = 0.3f)
                        ),
                        enabled = allFieldsValid(
                            nombreValid, emailValid, passwordValid, confirmPasswordValid,
                            viewModel.nombre.value, viewModel.email.value,
                            viewModel.password.value, viewModel.confirmPassword.value
                        )
                    ) {
                        Text(
                            "Registrarse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link a Login
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia Sesión",
                    color = VerdePrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Espacio inferior
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

// Componente para indicador de fortaleza de contraseña
@Composable
fun PasswordStrengthIndicator(strength: Int) {
    val strengthText = when (strength) {
        0 -> "Muy débil"
        1 -> "Débil"
        2 -> "Media"
        3 -> "Fuerte"
        else -> ""
    }

    val strengthColor = when (strength) {
        0 -> ErrorColor
        1 -> Color(0xFFFFA500) // Naranja
        2 -> Color(0xFFFFD700) // Amarillo
        3 -> VerdePrincipal
        else -> TextoSecundario
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Barra de progreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index < strength) strengthColor
                            else TextoSecundario.copy(alpha = 0.2f)
                        )
                )
            }
        }

        // Texto indicador
        Text(
            text = "Fortaleza: $strengthText",
            fontSize = 12.sp,
            color = strengthColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Función helper para validar todos los campos
fun allFieldsValid(
    nombreValid: Boolean,
    emailValid: Boolean,
    passwordValid: Boolean,
    confirmPasswordValid: Boolean,
    nombre: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    return nombreValid && emailValid && passwordValid && confirmPasswordValid &&
            nombre.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            password == confirmPassword
}