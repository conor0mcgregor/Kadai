package com.example.kadai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kadai.ui.screens.home.HomeScreen
import com.example.kadai.ui.screens.login.LoginScreen

/**
 * Grafo de navegación centralizado de Kadai.
 *
 * Para agregar nuevas pantallas: añadir el composable() correspondiente aquí
 * y registrar la ruta en Routes.kt. No es necesario modificar MainActivity.
 */
@Composable
fun KadaiNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { /* TODO: registrar ruta cuando exista la pantalla */ },
                onForgotClick   = { /* TODO: registrar ruta cuando exista la pantalla */ }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                userName        = "Alex García",
                userInitial     = "A",
                userPoints      = 1840,
                userRankPosition = 2,
                challengesCount = 12,
                streakDays      = 7,
                notifCount      = 3
            )
        }
    }
}
