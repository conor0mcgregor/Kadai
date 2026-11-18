package com.example.kadai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kadai.app.KadaiApp
import com.example.kadai.ui.theme.KadaiTheme

/**
 * Punto de entrada limpio de la aplicación.
 * Solo configura edge-to-edge, aplica el tema Kadai y delega la interfaz en KadaiApp.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KadaiTheme {
                KadaiApp()
            }
        }
    }
}
