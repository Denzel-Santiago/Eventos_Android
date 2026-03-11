package com.proyecto.eventos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.eventos.features.auth.presentation.screens.LoginScreen
import com.proyecto.eventos.features.auth.presentation.screens.RegistroScreen
import com.proyecto.eventos.features.eventos.presentation.screens.AdminEventosScreen
import com.proyecto.eventos.features.eventos.presentation.screens.CompraListScreen
import com.proyecto.eventos.features.favoritos.presentation.screens.FavoritosScreen
import com.proyecto.eventos.features.panel.presentation.screens.PanelScreen

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("panel") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate("panel") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("panel") {
            PanelScreen(navController = navController)
        }

        composable("eventos") {
            CompraListScreen(navController = navController)
        }

        composable("favoritos") {
            FavoritosScreen(navController = navController)
        }

        composable("admin/eventos") {
            AdminEventosScreen(navController = navController)
        }

        // placeholder historial — lo conectamos en siguiente feature
        composable("historial") {
            PanelScreen(navController = navController)
        }

        // placeholder verificacion — lo conectamos en feature compras
        composable("verificacion/{eventoId}") {
            PanelScreen(navController = navController)
        }
    }
}
