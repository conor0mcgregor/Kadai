package com.example.kadai.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kadai.ui.components.KadaiBottomNav
import com.example.kadai.ui.navigation.KadaiNavHost
import com.example.kadai.ui.navigation.Routes
import com.example.kadai.ui.theme.Background

/**
 * Contenedor principal de la aplicación.
 * Centraliza la estructura general con Scaffold, NavHost y la barra inferior compartida.
 */
@Composable
fun KadaiApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // La barra inferior solo aparece en los destinos principales implementados.
    val mainRoutes = setOf(Routes.HOME, Routes.GROUPS)
    val shouldShowBottomBar = currentRoute in mainRoutes

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (shouldShowBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Background.copy(alpha = 0.95f), Background),
                                startY = 0f,
                                endY = 160f
                            )
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    KadaiBottomNav(
                        currentRoute = currentRoute ?: Routes.HOME,
                        notifCount = 0,
                        availableRoutes = setOf("home", "groups", "challenge"),
                        onRouteChange = { route ->
                            when (route) {
                                "home" -> navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                "groups" -> navController.navigate(Routes.GROUPS) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                "challenge" -> navController.navigate(Routes.CHALLENGE_TODAY)
                                else -> Unit
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            KadaiNavHost(navController = navController)
        }
    }
}
