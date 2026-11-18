package com.example.kadai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.kadai.ui.navigation.KadaiNavHost
import com.example.kadai.ui.theme.KadaiTheme

/**
 * Punto de entrada de la aplicación.
 * Solo configura el tema y delega en KadaiNavHost toda la navegación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KadaiTheme {
                val navController = rememberNavController()
                KadaiNavHost(navController = navController)
            }
        }
    }
}
