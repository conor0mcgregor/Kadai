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

    // La barra inferior se muestra únicamente en destinos principales (Home)
    val shouldShowBottomBar = currentRoute == Routes.HOME

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
                        currentRoute = "home",
                        notifCount = 3,
                        onRouteChange = { route ->
                            when (route) {
                                "home" -> {
                                    if (currentRoute != Routes.HOME) {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.HOME) { inclusive = true }
                                        }
                                    }
                                }
                                // Las futuras pantallas (groups, challenge, notifs, profile)
                                // se conectarán aquí cuando existan sus destinos en Routes
                                else -> {}
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
