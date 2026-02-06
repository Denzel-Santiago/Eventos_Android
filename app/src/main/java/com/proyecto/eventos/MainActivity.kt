package com.proyecto.eventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.proyecto.eventos.core.navigation.NavegacionApp
import com.proyecto.eventos.ui.theme.EventosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventosTheme {
                NavegacionApp()
            }
        }
    }
}