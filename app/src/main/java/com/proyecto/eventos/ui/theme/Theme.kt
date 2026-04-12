package com.proyecto.eventos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipal,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onPrimary = Color.Black,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipalClaro,
    background = FondoClaro,
    surface = SuperficieClara,
    onPrimary = Color.White,
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

@Composable
fun SweepTicketsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Mantengo EventosTheme por compatibilidad si es necesario, 
// pero ahora delegando en SweepTicketsTheme
@Composable
fun EventosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SweepTicketsTheme(darkTheme = darkTheme, content = content)
}