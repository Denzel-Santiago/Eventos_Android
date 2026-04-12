package com.proyecto.eventos.features.auth.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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

    var nombreValid by remember { mutableStateOf(true) }
    var emailValid by remember { mutableStateOf(true) }
    var passwordValid by remember { mutableStateOf(true) }
    var confirmPasswordValid by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordStrength by remember { mutableStateOf(0) }

    val (nombreFocus, emailFocus, passwordFocus, confirmPasswordFocus) = FocusRequester.createRefs()

    val enterTransition = remember {
        slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400))
    }

    fun calculatePasswordStrength(password: String): Int {
        return when {
            password.length < 6 -> 0
            password.length < 8 -> 1
            password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).+$")) -> 3
            password.matches(Regex("^(?=.*[A-Z])(?=.*[a-z]).+$")) -> 2
            else -> 1
        }
    }

    LaunchedEffect(viewModel.isSuccess.value) {
        if (viewModel.isSuccess.value) {
            onRegisterSuccess()
        }
    }

    LaunchedEffect(viewModel.password.value) {
        passwordStrength = calculatePasswordStrength(viewModel.password.value)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface),
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
            Spacer(modifier = Modifier.weight(0.2f))

            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
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
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Únete a Sweeper Tickets",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        letterSpacing = 0.25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre
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
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .focusRequester(nombreFocus),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorTextColor = MaterialTheme.colorScheme.error,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error
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
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
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
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .focusRequester(emailFocus),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorTextColor = MaterialTheme.colorScheme.error,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error
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
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
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
                                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
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
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                            errorTextColor = MaterialTheme.colorScheme.error,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error
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
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    )

                    if (viewModel.password.value.isNotEmpty() && passwordValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordStrengthIndicator(strength = passwordStrength)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Contraseña
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
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible)
                                    Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
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
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorTextColor = MaterialTheme.colorScheme.error,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error
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
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )
            }

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
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = viewModel.error.value ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = true,
                enter = enterTransition
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
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
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
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

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia Sesión",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

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
        0 -> MaterialTheme.colorScheme.error
        1 -> Color(0xFFFFA500)
        2 -> Color(0xFFFFD700)
        3 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
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
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        )
                )
            }
        }

        Text(
            text = "Fortaleza: $strengthText",
            fontSize = 12.sp,
            color = strengthColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

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