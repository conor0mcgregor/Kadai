package com.example.kadai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.theme.BorderStrong
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextGhost

/**
 * Barra de navegación inferior compartida de Kadai.
 *
 * Contiene exactamente los cinco ítems del diseño original:
 * Inicio · Grupos · Reto (FAB central) · Notificaciones · Perfil.
 *
 * Puede usarse desde cualquier destino que deba mostrarla.
 * No gestiona estado de navegación internamente.
 *
 * @param currentRoute    Ruta actualmente seleccionada.
 * @param notifCount      Número de notificaciones no leídas (badge).
 * @param onRouteChange   Callback invocado al pulsar un ítem.
 */
@Composable
fun KadaiBottomNav(
    currentRoute: String = "home",
    notifCount: Int = 0,
    onRouteChange: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Surface.copy(alpha = 0.96f))
            .border(1.dp, BorderStrong, RoundedCornerShape(26.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. INICIO
            KadaiNavItem(
                icon = Icons.Outlined.Home,
                label = "Inicio",
                route = "home",
                isSelected = currentRoute == "home",
                onClick = { onRouteChange("home") }
            )

            // 2. GRUPOS
            KadaiNavItem(
                icon = Icons.Outlined.Group,
                label = "Grupos",
                route = "groups",
                isSelected = currentRoute == "groups",
                onClick = { onRouteChange("groups") }
            )

            // 3. RETO (Botón central protagonista / FAB)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, Color(0xFF9333EA))
                        )
                    )
                    .border(2.dp, PurpleSoft.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .clickable { onRouteChange("challenge") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FlashOn,
                    contentDescription = "Reto del día",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. NOTIFICACIONES
            KadaiNavItem(
                icon = Icons.Outlined.Notifications,
                label = "Avisos",
                route = "notifs",
                isSelected = currentRoute == "notifs",
                badgeCount = notifCount,
                onClick = { onRouteChange("notifs") }
            )

            // 5. PERFIL
            KadaiNavItem(
                icon = Icons.Outlined.Person,
                label = "Perfil",
                route = "profile",
                isSelected = currentRoute == "profile",
                onClick = { onRouteChange("profile") }
            )
        }
    }
}

@Composable
private fun KadaiNavItem(
    icon: ImageVector,
    label: String,
    route: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary.copy(alpha = 0.16f) else Color.Transparent,
        label = "nav_bg"
    )
    val iconTint  = if (isSelected) PurpleSoft else TextGhost
    val textColor = if (isSelected) PurpleSoft else TextGhost

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = PurplePrimary,
                            contentColor   = Color.White
                        ) {
                            Text(
                                text  = if (badgeCount < 10) "$badgeCount" else "9+",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector     = icon,
                    contentDescription = label,
                    tint            = iconTint,
                    modifier        = Modifier.size(22.dp)
                )
            }

            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize   = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = textColor
            )
        }
    }
}
