package com.example.kadai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.kadai.ui.screens.home.HomeScreen
import com.example.kadai.ui.screens.login.LoginScreen
import com.example.kadai.ui.theme.KadaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KadaiApp()
        }
    }
}

/**
 * Punto de entrada de la app.
 * Gestiona la navegación básica Login → Home para esta fase de diseño.
 *
 * En fases posteriores se sustituirá por NavHost con Navigation Compose
 * y autenticación real vía Firebase Authentication.
 */
@Composable
fun KadaiApp() {
    KadaiTheme {
        // Estado provisional de autenticación — se reemplazará con ViewModel + Firebase
        var isLoggedIn by rememberSaveable { mutableStateOf(false) }

        if (isLoggedIn) {
            HomeScreen(
                userName = "Alex García",
                userInitial = "A",
                userPoints = 1840,
                userRankPosition = 2,
                challengesCount = 12,
                streakDays = 7,
                notifCount = 3
            )
        } else {
            LoginScreen(
                onLoginClick = { _, _ ->
                    // TODO: conectar con Firebase Authentication
                    isLoggedIn = true
                },
                onGoogleClick = {
                    // TODO: Google Sign-In
                    isLoggedIn = true
                },
                onRegisterClick = {
                    // TODO: navegar a pantalla de registro
                },
                onForgotClick = {
                    // TODO: pantalla de recuperación de contraseña
                }
            )
        }
    }
}

@Preview(
    name = "KADAI App",
    showBackground = true,
    backgroundColor = 0xFF0F0F12
)
@Composable
private fun KadaiAppPreview() {
    KadaiApp()
}
