//com.proyecto.eventos.features.auth.presentation.screens.LoginScreen.kt
package com.proyecto.eventos.features.auth.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.proyecto.eventos.R
import com.proyecto.eventos.features.auth.presentation.viewmodels.LoginViewModel
import com.proyecto.eventos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Estados para animaciones
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface),
                    startY = 0f,
                    endY = screenHeight.value * 0.3f
                )
            )
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espacio superior flexible
        Spacer(modifier = Modifier.weight(0.3f))

        // Logo o Imagen con animación
        AnimatedContent(
            targetState = true,
            transitionSpec = {
                fadeIn(animationSpec = tween(800)) togetherWith
                        fadeOut(animationSpec = tween(800))
            },
            label = "logo_content"
        ) { targetVisible ->
            if (targetVisible) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoboletos),
                        contentDescription = "Logo Sweeper Tickets",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título con animación
        AnimatedContent(
            targetState = true,
            transitionSpec = {
                (slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))) togetherWith
                        fadeOut()
            },
            label = "title_content"
        ) { targetVisible ->
            if (targetVisible) {
                Text(
                    text = "Sweeper Tickets",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido de vuelta",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            letterSpacing = 0.25.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo de Email
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = {
                viewModel.onEmailChange(it)
                isEmailValid = it.contains("@") && it.contains(".")
            },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = if (isEmailValid && viewModel.email.value.isNotEmpty())
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
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
            isError = viewModel.email.value.isNotEmpty() && !isEmailValid,
            supportingText = {
                if (viewModel.email.value.isNotEmpty() && !isEmailValid) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = {
                viewModel.onPasswordChange(it)
                isPasswordValid = it.length >= 6
            },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isPasswordValid && viewModel.password.value.isNotEmpty())
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible)
                            "Ocultar contraseña" else "Mostrar contraseña",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
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
                    if (isEmailValid && isPasswordValid) {
                        viewModel.login(onLoginSuccess)
                    }
                }
            ),
            isError = viewModel.password.value.isNotEmpty() && !isPasswordValid,
            supportingText = {
                if (viewModel.password.value.isNotEmpty() && !isPasswordValid) {
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

        // Mensaje de error
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

        // Botón de Login
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        } else {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(onLoginSuccess)
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
                enabled = isEmailValid && isPasswordValid &&
                        viewModel.email.value.isNotEmpty() &&
                        viewModel.password.value.isNotEmpty()
            ) {
                Text(
                    "Iniciar Sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate("registro") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.animateContentSize()
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}