//com.proyecto.eventos.core.navigation.NavegacionApp.kt
package com.proyecto.eventos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.eventos.features.auth.presentation.screens.LoginScreen
import com.proyecto.eventos.features.auth.presentation.screens.RegistroScreen
import com.proyecto.eventos.features.compras.presentation.screens.VerificacionIdentidadScreen
import com.proyecto.eventos.features.eventos.presentation.screens.AdminEventosScreen
import com.proyecto.eventos.features.eventos.presentation.screens.CompraListScreen
import com.proyecto.eventos.features.favoritos.presentation.screens.FavoritosScreen
import com.proyecto.eventos.features.historial.presentation.screens.HistorialScreen
import com.proyecto.eventos.features.panel.presentation.screens.PanelScreen
import com.proyecto.eventos.features.admin.presentation.screens.AdminUsuariosScreen
import com.proyecto.eventos.features.notifications.presentation.screens.NotificationsScreen

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

        composable("historial") {
            HistorialScreen(navController = navController)
        }

        composable("admin/eventos") {
            AdminEventosScreen(navController = navController)
        }

        composable("admin/usuarios") {
            AdminUsuariosScreen(navController = navController)
        }

        composable("notificaciones") {
            NotificationsScreen(navController = navController)
        }

        composable(
            route = "verificacion/{eventoId}/{nombreEvento}/{fecha}/{hora}/{precio}",
            arguments = listOf(
                navArgument("eventoId") { type = NavType.StringType },
                navArgument("nombreEvento") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType },
                navArgument("precio") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            VerificacionIdentidadScreen(
                navController = navController,
                eventoId = backStackEntry.arguments?.getString("eventoId") ?: "",
                nombreEvento = backStackEntry.arguments?.getString("nombreEvento") ?: "",
                fecha = backStackEntry.arguments?.getString("fecha") ?: "",
                hora = backStackEntry.arguments?.getString("hora") ?: "",
                precio = backStackEntry.arguments?.getFloat("precio")?.toDouble() ?: 0.0
            )
        }
    }
}
