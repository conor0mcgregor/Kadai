package com.example.kadai.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.BorderStrong
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextGhost
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.TextSecondary
import com.example.kadai.ui.theme.Warning

// ── Modelos de datos para Home ────────────────────────────────────────────────

data class GroupItem(
    val id: String,
    val name: String,
    val emoji: String,
    val memberCount: Int,
    val gradientStart: Color,
    val gradientEnd: Color,
    val isActive: Boolean = false
)

data class ActivityItem(
    val id: String,
    val userName: String,
    val userInitial: String,
    val avatarColor: Color,
    val action: String,
    val groupName: String,
    val timestamp: String,
    val pointsGained: Int? = null,
    val reactions: String? = null,
    val hasProof: Boolean = false,
    val proofEmoji: String = "",
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val userVotedUp: Boolean = false
)

private val sampleGroups = listOf(
    GroupItem("1", "Pandilla",     "🏆", 5, Color(0xFF6D28D9), Color(0xFF8B5CF6), isActive = true),
    GroupItem("2", "Uni 2024",     "🎯", 3, Color(0xFF047857), Color(0xFF10B981)),
    GroupItem("3", "Curro Squad",  "⚡", 8, Color(0xFFC2410C), Color(0xFFF97316)),
    GroupItem("4", "Músicos",      "🎵", 4, Color(0xFF1E3A8A), Color(0xFF3B82F6)),
)

private val sampleActivity = listOf(
    ActivityItem(
        id = "1",
        userName = "María R.",
        userInitial = "M",
        avatarColor = Color(0xFF7C3AED),
        action = "Subió su prueba diaria",
        groupName = "Pandilla",
        timestamp = "hace 14 min",
        hasProof = true,
        proofEmoji = "🌍",
        upvotes = 8,
        downvotes = 1,
        userVotedUp = true
    ),
    ActivityItem(
        id = "2",
        userName = "Carlos M.",
        userInitial = "C",
        avatarColor = Color(0xFF059669),
        action = "Superó el reto y escaló posición",
        groupName = "Uni 2024",
        timestamp = "hace 1 h",
        pointsGained = 220
    ),
    ActivityItem(
        id = "3",
        userName = "Sara L.",
        userInitial = "S",
        avatarColor = Color(0xFFD97706),
        action = "Reaccionó a tu prueba",
        groupName = "Pandilla",
        timestamp = "hace 2 h",
        reactions = "🔥  👏  🚀"
    ),
)

// ── Pantalla Home Principal ───────────────────────────────────────────────────

/**
 * Pantalla principal (Home) de KADAI.
 *
 * Mejoras aplicadas:
 * - Insets superiores seguros para no solapar los iconos de la barra de estado del móvil (`statusBarsPadding()`).
 * - Barra de navegación con los 5 ítems exactos: Inicio, Grupos, Reto (FAB), Notificaciones, Perfil.
 * - Insets inferiores seguros para gestos del sistema (`navigationBarsPadding()`).
 * - Diseño visual modernizado, con mayor claridad, contrastes depurados y jerarquía limpia.
 */
@Composable
fun HomeScreen(
    userName: String = "Alex García",
    userInitial: String = "A",
    userPoints: Int = 1840,
    userRankPosition: Int = 2,
    challengesCount: Int = 12,
    streakDays: Int = 7,
    groups: List<GroupItem> = sampleGroups,
    activity: List<ActivityItem> = sampleActivity,
    notifCount: Int = 3,
    onGroupClick: (GroupItem) -> Unit = {},
    onChallengeClick: () -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    onNotifClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var currentNavRoute by rememberSaveable { mutableStateOf("home") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Resplandor sutil de fondo superior para dar profundidad visual
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PurplePrimary.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(300f, -50f),
                        radius = 500f
                    )
                )
        )

        // Contenido scrollable con padding superior para la barra de estado
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(), // Protege los iconos del móvil (hora, batería, notch, etc.)
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 120.dp // Espacio para la barra de navegación flotante inferior
            )
        ) {
            // ── Cabecera Principal ──────────────────────────────────────────
            item {
                ModernHomeHeader(
                    userName = userName,
                    userInitial = userInitial,
                    streakDays = streakDays,
                    onAvatarClick = onProfileClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Resumen de Estadísticas del Usuario ─────────────────────────
            item {
                ModernStatsStrip(
                    points = userPoints,
                    rankPosition = userRankPosition,
                    challengesCount = challengesCount,
                    streakDays = streakDays,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(26.dp))
            }

            // ── Carrusel de Grupos ──────────────────────────────────────────
            item {
                ModernSectionHeader(
                    title = "Mis grupos",
                    badgeText = "${groups.size}",
                    actionLabel = "Ver todos",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                ModernGroupsRow(
                    groups = groups,
                    onGroupClick = onGroupClick,
                    onAddGroupClick = onAddGroupClick
                )
                Spacer(modifier = Modifier.height(26.dp))
            }

            // ── Tarjeta Protagonista del Reto Diario ────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Reto del día",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = TextPrimary
                        )
                        PulsingLiveBadge()
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ModernDailyChallengeCard(
                    title = "Aprende a decir \"gracias\" en 5 idiomas",
                    description = "Graba un video corto o foto mostrando cómo pronuncias 'gracias' en 5 lenguas diferentes. ¡Suma puntos por rapidez!",
                    timeRemaining = "08:24:15",
                    participantsText = "4 de 5 han participado",
                    onChallengeClick = onChallengeClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(26.dp))
            }

            // ── Actividad Reciente de Amigos ────────────────────────────────
            item {
                ModernSectionHeader(
                    title = "Actividad reciente",
                    actionLabel = "Filtrar",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(activity) { item ->
                ModernActivityCard(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }

        // ── Barra de Navegación Inferior Flotante ───────────────────────────
        // Insets de navegación protegidos para que los gestos de Android no la tapen
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Background.copy(alpha = 0.95f), Background),
                        startY = 0f,
                        endY = 160f
                    )
                )
                .navigationBarsPadding() // Respeta barra de gestos / botones de navegación Android
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            ModernBottomNav(
                currentRoute = currentNavRoute,
                notifCount = notifCount,
                onRouteChange = { route ->
                    currentNavRoute = route
                    when (route) {
                        "notifs" -> onNotifClick()
                        "profile" -> onProfileClick()
                        "challenge" -> onChallengeClick()
                    }
                }
            )
        }
    }
}

// ── Componentes de la Cabecera ────────────────────────────────────────────────

@Composable
private fun ModernHomeHeader(
    userName: String,
    userInitial: String,
    streakDays: Int,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar con aro gradiente y presencia activa
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, Color(0xFF9333EA))
                        )
                    )
                    .border(2.dp, PurpleSoft.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitial,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )

                // Indicador de conexión en vivo
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Success)
                        .border(1.5.dp, Background, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Column {
                Text(
                    text = "HOLA DE NUEVO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PurpleSoft
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = TextPrimary
                )
            }
        }

        // Pill con racha de días activa
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Warning.copy(alpha = 0.12f))
                .border(1.dp, Warning.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🔥", fontSize = 13.sp)
            Text(
                text = "$streakDays d",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Warning
            )
        }
    }
}

// ── Componente de Métricas / Stats Strip ──────────────────────────────────────

@Composable
private fun ModernStatsStrip(
    points: Int,
    rankPosition: Int,
    challengesCount: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Surface,
                        Color(0xFF1B1824)
                    )
                )
            )
            .border(1.dp, BorderStrong, RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puntos
            ModernStatCol(
                value = String.format("%,d", points).replace(',', '.'),
                label = "PUNTOS",
                valueColor = PurpleSoft
            )

            ModernStatDivider()

            // Posición en ranking
            ModernStatCol(
                value = "#$rankPosition",
                label = "RANKING",
                valueColor = Color.White,
                iconPrefix = "👑 "
            )

            ModernStatDivider()

            // Retos completados
            ModernStatCol(
                value = "$challengesCount",
                label = "RETOS",
                valueColor = Color.White
            )
        }
    }
}

@Composable
private fun ModernStatCol(
    value: String,
    label: String,
    valueColor: Color,
    iconPrefix: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (iconPrefix.isNotEmpty()) {
                Text(text = iconPrefix, fontSize = 13.sp)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp
                ),
                color = valueColor
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextDisabled
        )
    }
}

@Composable
private fun ModernStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Border)
    )
}

// ── Cabecera de Secciones ─────────────────────────────────────────────────────

@Composable
private fun ModernSectionHeader(
    title: String,
    actionLabel: String? = null,
    badgeText: String? = null,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                ),
                color = TextPrimary
            )
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = TextMuted
                    )
                }
            }
        }

        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleSoft
                ),
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// ── Carrusel de Grupos ────────────────────────────────────────────────────────

@Composable
private fun ModernGroupsRow(
    groups: List<GroupItem>,
    onGroupClick: (GroupItem) -> Unit,
    onAddGroupClick: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(groups) { group ->
            ModernGroupPill(group = group, onClick = { onGroupClick(group) })
        }
    }
}

@Composable
private fun ModernGroupPill(
    group: GroupItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(modifier = Modifier.size(60.dp)) {
            // Contenedor del avatar con gradiente y borde activo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(group.gradientStart, group.gradientEnd)
                        )
                    )
                    .border(
                        width = if (group.isActive) 2.dp else 1.dp,
                        color = if (group.isActive) PurpleSoft else Border,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = group.emoji, fontSize = 24.sp)
            }

            // Badge de cantidad de integrantes
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (group.isActive) PurplePrimary else Color(0xFF262633))
                    .border(1.5.dp, Background, RoundedCornerShape(8.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${group.memberCount}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }
        }

        Text(
            text = group.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (group.isActive) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (group.isActive) TextPrimary else TextDisabled,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Indicador En Vivo / Pulsante ──────────────────────────────────────────────

@Composable
private fun PulsingLiveBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Warning.copy(alpha = 0.12f))
            .border(1.dp, Warning.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Warning.copy(alpha = alpha))
        )
        Text(
            text = "ACTIVO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = Warning
        )
    }
}

// ── Tarjeta Protagonista del Reto Diario ───────────────────────────────────────

@Composable
fun ModernDailyChallengeCard(
    title: String,
    description: String,
    timeRemaining: String,
    participantsText: String,
    onChallengeClick: () -> Unit,
    modifier: Modifier = Modifier,
    alreadySubmitted: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(1.dp, BorderStrong, RoundedCornerShape(24.dp))
    ) {
        // Cabecera con degradado distintivo KADAI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF581C87), // Púrpura profundo
                            Color(0xFF7C3AED), // Morado acento
                            Color(0xFF9333EA)  // Violeta vibrante
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Elemento geométrico sutil de fondo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .align(Alignment.TopEnd)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Fila superior: Tag de categoría + Contador regresivo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✦  RETO GLOBAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }

                    // Contador de tiempo
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timeRemaining,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                // Título del reto
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White
                )
            }
        }

        // Cuerpo de la tarjeta
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(18.dp))

            // Botón de acción principal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (!alreadySubmitted)
                            Brush.linearGradient(
                                colors = listOf(PurplePrimary, Color(0xFF9333EA))
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(Success, Color(0xFF16A34A))
                            )
                    )
                    .clickable { onChallengeClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (!alreadySubmitted) "＋" else "✓",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = if (!alreadySubmitted) "Subir mi prueba" else "Prueba enviada · Ver",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernMetaChip(text: String, icon: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TextMuted
        )
    }
}

// ── Tarjeta de Actividad Social ───────────────────────────────────────────────

@Composable
fun ModernActivityCard(
    item: ActivityItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Thumbnail multimedia o avatar de usuario
        if (item.hasProof) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F766E))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.proofEmoji, fontSize = 22.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.userInitial,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }
        }

        // Información de actividad
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = TextPrimary
                )
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGhost
                )
            }

            Text(
                text = "${item.action}  •  ${item.groupName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Votaciones inline si tiene prueba
            if (item.hasProof) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModernVoteChip(
                        count = item.upvotes,
                        isPositive = true,
                        isSelected = item.userVotedUp
                    )
                    ModernVoteChip(
                        count = item.downvotes,
                        isPositive = false,
                        isSelected = false
                    )
                }
            }

            // Puntos ganados
            if (item.pointsGained != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Success.copy(alpha = 0.12f))
                        .border(1.dp, Success.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⭐", fontSize = 10.sp)
                    Text(
                        text = "+${item.pointsGained} pts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Success
                    )
                }
            }

            // Reacciones
            if (item.reactions != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.reactions, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun ModernVoteChip(
    count: Int,
    isPositive: Boolean,
    isSelected: Boolean
) {
    val bgColor = if (isSelected) PurplePrimary.copy(alpha = 0.18f) else SurfaceElevated
    val borderColor = if (isSelected) PurpleSoft else Border
    val textColor = if (isSelected) PurpleSoft else TextMuted

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
            .clickable { /* Toggle vote */ },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (isPositive) "▲" else "▼",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = textColor
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

// ── Barra de Navegación Inferior Flotante (5 Ítems) ───────────────────────────

/**
 * Barra inferior con los 5 ítems exactos solicitados:
 * 1. inicio
 * 2. grupos
 * 3. reto (FAB central destacado)
 * 4. notificaciones
 * 5. perfil
 */
@Composable
fun ModernBottomNav(
    currentRoute: String = "home",
    notifCount: Int = 3,
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
            ModernNavItem(
                icon = Icons.Outlined.Home,
                label = "Inicio",
                route = "home",
                isSelected = currentRoute == "home",
                onClick = { onRouteChange("home") }
            )

            // 2. GRUPOS
            ModernNavItem(
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
            ModernNavItem(
                icon = Icons.Outlined.Notifications,
                label = "Avisos",
                route = "notifs",
                isSelected = currentRoute == "notifs",
                badgeCount = notifCount,
                onClick = { onRouteChange("notifs") }
            )

            // 5. PERFIL
            ModernNavItem(
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
private fun ModernNavItem(
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
    val iconTint = if (isSelected) PurpleSoft else TextGhost
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
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (badgeCount < 10) "$badgeCount" else "9+",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = textColor
            )
        }
    }
}

// ── Previsualización ──────────────────────────────────────────────────────────

@Preview(
    name = "Home Screen Modernizada",
    showBackground = true,
    backgroundColor = 0xFF0F0F12,
    device = "spec:width=390dp,height=844dp,dpi=460"
)
@Composable
private fun HomeScreenPreview() {
    KadaiTheme {
        HomeScreen()
    }
}
