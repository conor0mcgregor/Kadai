package com.example.kadai.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextGhost
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

// ── Modelos de datos para preview ────────────────────────────────────────────

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
    val proofEmoji: String = ""
)

private val sampleGroups = listOf(
    GroupItem("1", "Pandilla",     "🏆", 5, Color(0xFF5B21B6), Color(0xFF7C3AED), isActive = true),
    GroupItem("2", "Uni 2024",     "🎯", 3, Color(0xFF064E3B), Color(0xFF065F46)),
    GroupItem("3", "Curro Squad",  "⚡", 8, Color(0xFF7C2D12), Color(0xFF92400E)),
    GroupItem("4", "Músicos",      "🎵", 4, Color(0xFF1E1B4B), Color(0xFF312E81)),
)

private val sampleActivity = listOf(
    ActivityItem("1", "María R.", "M", Color(0xFF5B21B6), "Subió su prueba", "Pandilla",     "hace 12m", hasProof = true, proofEmoji = "🌍"),
    ActivityItem("2", "Carlos M.","C", Color(0xFF064E3B), "Subió en ranking", "Uni 2024",   "hace 1h",  pointsGained = 220),
    ActivityItem("3", "Sara L.",  "S", Color(0xFF7C2D12), "Reaccionó a tu prueba", "Pandilla","hace 2h", reactions = "🔥 🎉 👏"),
)

// ── Pantalla Home ─────────────────────────────────────────────────────────────

/**
 * Pantalla principal (Home) de KADAI.
 *
 * @param userName          Nombre del usuario
 * @param userInitial       Inicial para el avatar
 * @param userPoints        Puntos acumulados del usuario (grupo activo)
 * @param userRankPosition  Posición en el ranking
 * @param challengesCount   Número de retos completados
 * @param streakDays        Días de racha consecutivos
 * @param groups            Lista de grupos del usuario
 * @param activity          Feed de actividad reciente
 * @param notifCount        Número de notificaciones sin leer
 * @param onGroupClick      Callback al seleccionar un grupo
 * @param onChallengeClick  Callback al pulsar "Subir mi prueba"
 * @param onAddGroupClick   Callback al pulsar el botón de nuevo grupo
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
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Background)
    ) {
        // Contenido scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            item {
                HomeHeader(
                    userName = userName,
                    userInitial = userInitial,
                    notifCount = notifCount,
                    onNotifClick = onNotifClick
                )
            }

            // ── Stats strip ─────────────────────────────────────────────────
            item {
                StatsStrip(
                    points = userPoints,
                    rankPosition = userRankPosition,
                    challengesCount = challengesCount,
                    streakDays = streakDays,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Grupos ───────────────────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Mis grupos",
                    actionLabel = "Ver todos",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                GroupsRow(
                    groups = groups,
                    onGroupClick = onGroupClick,
                    onAddGroupClick = onAddGroupClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Reto diario ──────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reto de hoy",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    ActiveIndicator()
                }
                Spacer(modifier = Modifier.height(12.dp))
                DailyChallengeCard(
                    title = "Aprende a decir \"gracias\" en 5 idiomas distintos",
                    description = "Graba o fotografía cómo dices \"gracias\" en 5 idiomas diferentes. ¡Prueba con los más originales!",
                    timeRemaining = "8h 23m",
                    participantsText = "4 de 5 participaron",
                    onChallengeClick = onChallengeClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Actividad reciente ───────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Actividad reciente",
                    actionLabel = "Ver más",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(activity) { item ->
                ActivityCard(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp)
                )
            }
        }

        // ── Bottom Navigation (superpuesto, flotante) ────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Background),
                        startY = 0f,
                        endY = 220f
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            KadaiBottomNav()
        }
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    userName: String,
    userInitial: String,
    notifCount: Int,
    onNotifClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, PurpleSoft)
                        )
                    )
                    .border(2.dp, PurpleSoft.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitial,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Buenos días,",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDisabled
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
            }
        }

        // Notificaciones
        IconButton(
            onClick = onNotifClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Surface)
                .border(1.5.dp, Border, RoundedCornerShape(12.dp))
        ) {
            BadgedBox(
                badge = {
                    if (notifCount > 0) {
                        Badge(
                            containerColor = PurplePrimary,
                            contentColor = Color.White
                        ) {
                            if (notifCount < 10) Text(notifCount.toString()) else Text("9+")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Stats Strip ───────────────────────────────────────────────────────────────

@Composable
private fun StatsStrip(
    points: Int,
    rankPosition: Int,
    challengesCount: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Surface, Color(0xFF1D1424))
                )
            )
            .border(1.5.dp, Border, RoundedCornerShape(18.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Puntos
        StatItem(
            value = points.toString(),
            label = "Puntos",
            valueColor = PurpleSoft
        )

        StatDivider()

        // Ranking
        StatItem(
            value = "#$rankPosition",
            label = "Posición",
            prefix = if (rankPosition == 1) "👑 " else ""
        )

        StatDivider()

        // Retos + racha
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StatItem(value = challengesCount.toString(), label = "Retos")
            Spacer(modifier = Modifier.height(4.dp))
            StreakBadge(days = streakDays)
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
    prefix: String = ""
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (prefix.isNotEmpty()) {
                Text(text = prefix, fontSize = 14.sp)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = valueColor
            )
        }
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Border)
    )
}

@Composable
private fun StreakBadge(days: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Warning.copy(alpha = 0.12f))
            .border(1.dp, Warning.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text("🔥", fontSize = 11.sp)
        Text(
            text = "$days días",
            style = MaterialTheme.typography.labelSmall,
            color = Warning,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = PurplePrimary,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// ── Groups Row ────────────────────────────────────────────────────────────────

@Composable
private fun GroupsRow(
    groups: List<GroupItem>,
    onGroupClick: (GroupItem) -> Unit,
    onAddGroupClick: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(groups) { group ->
            GroupPill(group = group, onClick = { onGroupClick(group) })
        }
        item {
            // Botón añadir grupo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { onAddGroupClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, Border, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Nuevo grupo",
                        tint = Color(0xFF3E3E50),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Nuevo",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun GroupPill(
    group: GroupItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(group.gradientStart, group.gradientEnd)
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = if (group.isActive) PurplePrimary else Border,
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = group.emoji, fontSize = 22.sp)
            }

            // Badge de miembros
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (group.isActive) PurplePrimary else Color(0xFF2A2A35))
                    .border(
                        1.5.dp,
                        Background,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${group.memberCount}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (group.isActive) Color.White else TextDisabled,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = group.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (group.isActive) TextPrimary else TextDisabled,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Active Indicator ──────────────────────────────────────────────────────────

@Composable
private fun ActiveIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "active_dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Warning.copy(alpha = alpha))
        )
        Text(
            text = "Activo",
            style = MaterialTheme.typography.labelMedium,
            color = Warning,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Daily Challenge Card ──────────────────────────────────────────────────────

@Composable
fun DailyChallengeCard(
    title: String,
    description: String,
    timeRemaining: String,
    participantsText: String,
    onChallengeClick: () -> Unit,
    modifier: Modifier = Modifier,
    alreadySubmitted: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, Border, RoundedCornerShape(20.dp))
    ) {
        Column {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF5B21B6),
                                Color(0xFF7C3AED),
                                Color(0xFF9333EA)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                // Círculo decorativo de fondo
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                )

                Column {
                    // Tag "Reto del día"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✦  RETO DEL DÍA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Título
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ActiveDot(color = Color(0xFFFDE68A))
                        Text(
                            text = "Cierra en ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Text(
                            text = timeRemaining,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Meta chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetaChipSmall(text = "👥 $participantsText")
                    MetaChipSmall(text = "⭐ Cultura")
                    MetaChipSmall(text = "📸 Foto/vídeo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CTA button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                    Text(
                        text = if (!alreadySubmitted) "＋  Subir mi prueba" else "✓  Prueba enviada · Ver",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveDot(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "challenge_dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
private fun MetaChipSmall(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1E28))
            .border(1.dp, Border, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )
    }
}

// ── Activity Card ─────────────────────────────────────────────────────────────

@Composable
fun ActivityCard(
    item: ActivityItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.5.dp, Border, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar o thumbnail de prueba
        if (item.hasProof) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1E3A5F), Color(0xFF1E5F4E))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.proofEmoji, fontSize = 20.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(item.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.userInitial,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }
        }

        // Contenido
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.userName,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${item.action} · ${item.groupName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (item.hasProof) {
                Spacer(modifier = Modifier.height(6.dp))
                VoteRow()
            }

            if (item.pointsGained != null) {
                Spacer(modifier = Modifier.height(6.dp))
                PointsBadge(points = item.pointsGained)
            }

            if (item.reactions != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.reactions, fontSize = 16.sp)
            }
        }

        // Timestamp
        Text(
            text = item.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = TextGhost
        )
    }
}

@Composable
private fun VoteRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VoteButton(label = "8", isPositive = true, voted = true)
        VoteButton(label = "1", isPositive = false, voted = false)
    }
}

@Composable
private fun VoteButton(label: String, isPositive: Boolean, voted: Boolean) {
    val bg    = if (voted) PurplePrimary.copy(alpha = 0.15f) else Color(0xFF1E1E28)
    val bord  = if (voted) PurplePrimary.copy(alpha = 0.4f) else Border
    val color = if (voted) PurpleSoft else TextDisabled

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, bord, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clickable { /* TODO: votar */ },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = if (isPositive) "▲" else "▼",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun PointsBadge(points: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Success.copy(alpha = 0.12f))
            .border(1.dp, Success.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text("⭐", fontSize = 10.sp)
        Text(
            text = "+$points pts",
            style = MaterialTheme.typography.labelSmall,
            color = Success,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Bottom Navigation ─────────────────────────────────────────────────────────

@Composable
fun KadaiBottomNav(
    currentRoute: String = "home",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Surface.copy(alpha = 0.95f)
            )
            .border(1.5.dp, Border, RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(icon = Icons.Outlined.Home,          label = "Inicio",   route = "home",    current = currentRoute)
        NavItem(icon = Icons.Outlined.Group,         label = "Grupos",   route = "groups",  current = currentRoute)

        // FAB central — Reto
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PurplePrimary, Color(0xFF9333EA))
                    )
                )
                .border(2.dp, PurpleSoft.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FlashOn,
                contentDescription = "Reto del día",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        NavItem(icon = Icons.Outlined.BarChart, label = "Ranking",   route = "ranking", current = currentRoute)
        NavItem(icon = Icons.Outlined.Person,   label = "Perfil",    route = "profile", current = currentRoute)
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    route: String,
    current: String
) {
    val isActive = current == route
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) PurplePrimary.copy(alpha = 0.15f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) PurpleSoft else TextGhost,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (isActive) PurpleSoft else TextGhost,
                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(
    name = "Home Screen",
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
