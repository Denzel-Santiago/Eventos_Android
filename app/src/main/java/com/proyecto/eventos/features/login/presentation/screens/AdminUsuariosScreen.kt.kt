package com.proyecto.eventos.features.login.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.eventos.features.login.presentation.viewmodel.AdminUsuariosViewModel
import com.proyecto.eventos.features.login.presentation.viewmodel.AdminUsuariosViewModelFactory

@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    viewModel: AdminUsuariosViewModel = viewModel(factory = AdminUsuariosViewModelFactory())
) {

    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Administrar Usuarios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Funcionalidad en desarrollo")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}