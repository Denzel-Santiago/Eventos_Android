package com.proyecto.eventos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.eventos.features.eventos.presentation.screens.AdminEventosScreen
import com.proyecto.eventos.features.eventos.presentation.screens.CompraScreen
import com.proyecto.eventos.features.eventos.presentation.screens.InicioScreen
import com.proyecto.eventos.features.login.presentation.screens.AdminUsuariosScreen
import com.proyecto.eventos.features.login.presentation.screens.LoginScreen
import com.proyecto.eventos.features.login.presentation.screens.RegistroScreen


@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            InicioScreen(navController = navController)
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("registro") {
            RegistroScreen(navController = navController)
        }

        composable("compra") {
            CompraScreen(navController = navController)
        }

        composable("admin_eventos") {
            AdminEventosScreen(navController = navController)
        }

        composable("admin_usuarios") {
            AdminUsuariosScreen(navController = navController)
        }
    }
}