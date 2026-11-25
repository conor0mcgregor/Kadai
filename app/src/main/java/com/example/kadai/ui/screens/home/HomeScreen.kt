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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.usecase.formatCountdown
import com.example.kadai.ui.components.ChallengeCard
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiSectionHeader
import com.example.kadai.ui.components.ProofThumbnail
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.BorderStrong
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.TextSecondary
import com.example.kadai.ui.theme.Warning
import kotlinx.coroutines.delay

@Composable
fun HomeRoute(
    onGroupClick: (groupId: String) -> Unit,
    onChallengeClick: (dailyChallengeId: String) -> Unit,
    onGroupsClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onGroupClick = onGroupClick,
        onChallengeClick = onChallengeClick,
        onGroupsClick = onGroupsClick,
        onCreateGroupClick = onCreateGroupClick,
        onProfileClick = onProfileClick,
        onRetry = viewModel::retry
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onGroupClick: (groupId: String) -> Unit,
    onChallengeClick: (dailyChallengeId: String) -> Unit,
    onGroupsClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        BackgroundGlow()

        when {
            state.isLoading -> KadaiLoadingState(
                modifier = Modifier.statusBarsPadding(),
                message = "Preparando tu reto…"
            )
            state.error != null -> KadaiErrorState(
                message = state.error,
                onRetry = onRetry,
                modifier = Modifier.statusBarsPadding()
            )
            else -> HomeContent(
                state = state,
                onGroupClick = onGroupClick,
                onChallengeClick = onChallengeClick,
                onGroupsClick = onGroupsClick,
                onCreateGroupClick = onCreateGroupClick,
                onProfileClick = onProfileClick,
                now = System.currentTimeMillis()
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onGroupClick: (String) -> Unit,
    onChallengeClick: (String) -> Unit,
    onGroupsClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onProfileClick: () -> Unit,
    now: Long
) {
    val ticker by produceState(initialValue = now) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val challenge = state.challenge

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
    ) {
        item {
            HomeHeader(
                userName = state.userName,
                userInitial = state.userInitial,
                streakDays = state.streakDays,
                onAvatarClick = onProfileClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            StatsStrip(
                points = state.userPoints,
                rankPosition = state.userRankPosition,
                challengesCount = state.challengesCount,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(26.dp))
        }

        item {
            KadaiSectionHeader(
                title = "Mis grupos",
                badge = "${state.groups.size}",
                actionLabel = "Ver todos",
                onActionClick = onGroupsClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (state.groups.isEmpty()) {
            item {
                EmptyGroupsCard(
                    onCreateGroupClick = onCreateGroupClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(26.dp))
            }
        } else {
            item {
                GroupsRow(
                    groups = state.groups,
                    onGroupClick = onGroupClick,
                    onCreateGroupClick = onCreateGroupClick
                )
                Spacer(modifier = Modifier.height(26.dp))
            }
        }

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
                    if (challenge?.isOpen == true) PulsingLiveBadge()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (challenge == null) {
                KadaiCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Hoy no hay reto activo",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "El siguiente reto se publicará a lo largo del día.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }
            } else {
                val isOpen = challenge.isOpen && challenge.closesAt > ticker
                ChallengeCard(
                    title = challenge.title,
                    description = challenge.description,
                    timeRemaining = if (isOpen) {
                        formatCountdown(challenge.closesAt - ticker)
                    } else {
                        "Cerrado"
                    },
                    participantsText = challenge.participantsText,
                    alreadySubmitted = challenge.alreadySubmitted,
                    isOpen = isOpen,
                    category = challenge.category,
                    onActionClick = { onChallengeClick(challenge.dailyChallengeId) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(26.dp))
        }

        item {
            KadaiSectionHeader(
                title = "Actividad reciente",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (state.activity.isEmpty()) {
            item {
                KadaiCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Todavía no hay actividad",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cuando alguien de tus grupos suba una prueba aparecerá aquí.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }
            }
        } else {
            items(state.activity, key = { it.id }) { activity ->
                ActivityCard(
                    item = activity,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun BackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(PurplePrimary.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(300f, -50f),
                    radius = 500f
                )
            )
    )
}

@Composable
private fun HomeHeader(
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
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

@Composable
private fun StatsStrip(
    points: Int,
    rankPosition: Int?,
    challengesCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = listOf(Surface, Color(0xFF1B1824))))
            .border(1.dp, BorderStrong, RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumn(
                value = points.toString(),
                label = "PUNTOS",
                valueColor = PurpleSoft
            )
            StatDivider()
            StatColumn(
                value = rankPosition?.let { "#$it" } ?: "—",
                label = "RANKING",
                valueColor = Color.White
            )
            StatDivider()
            StatColumn(
                value = challengesCount.toString(),
                label = "RETOS",
                valueColor = Color.White
            )
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, valueColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp
            ),
            color = valueColor
        )
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
private fun StatDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 32.dp)
            .background(Border)
    )
}

@Composable
private fun GroupsRow(
    groups: List<HomeGroupItem>,
    onGroupClick: (String) -> Unit,
    onCreateGroupClick: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(groups, key = { it.id }) { group ->
            GroupPill(group = group, onClick = { onGroupClick(group.id) })
        }
        item {
            AddGroupPill(onClick = onCreateGroupClick)
        }
    }
}

@Composable
private fun GroupPill(group: HomeGroupItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(modifier = Modifier.size(60.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF8B5CF6)))
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

@Composable
private fun AddGroupPill(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceElevated)
                .border(1.dp, Border, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Crear grupo",
                tint = PurpleSoft,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Crear",
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled
        )
    }
}

@Composable
private fun EmptyGroupsCard(onCreateGroupClick: () -> Unit, modifier: Modifier = Modifier) {
    KadaiCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Group,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(26.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aún no perteneces a ningún grupo",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = "Crea uno o únete con un código para empezar a competir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDisabled
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Crear mi primer grupo",
            style = MaterialTheme.typography.labelLarge,
            color = PurpleSoft,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onCreateGroupClick() }
                .padding(vertical = 6.dp)
        )
    }
}

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

@Composable
private fun ActivityCard(item: HomeActivityItem, modifier: Modifier = Modifier) {
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
        ProofThumbnail(storagePath = item.id, mediaType = item.mediaType)

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
                    color = TextDisabled
                )
            }

            Text(
                text = "${item.action}  •  ${item.groupName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(
    name = "Home conectado",
    showBackground = true,
    backgroundColor = 0xFF0F0F12,
    device = "spec:width=390dp,height=844dp,dpi=460"
)
@Composable
private fun HomeScreenPreview() {
    KadaiTheme {
        HomeScreen(
            state = HomeUiState(
                isLoading = false,
                userName = "Alex García",
                userInitial = "A",
                userPoints = 1840,
                userRankPosition = 2,
                challengesCount = 12,
                streakDays = 7,
                groups = listOf(
                    HomeGroupItem("group_pandilla", "Pandilla", "🏆", 5, 1840, isActive = true),
                    HomeGroupItem("group_uni", "Uni 2024", "🎯", 3, 145),
                    HomeGroupItem("group_curro", "Curro Squad", "⚡", 2, 0)
                ),
                challenge = HomeChallengeItem(
                    dailyChallengeId = "daily_001",
                    title = "Grita en medio de la calle",
                    description = "Sube un vídeo gritando en medio de la calle. Cuanto antes lo hagas, más puntos te llevas.",
                    category = "Atrevido",
                    closesAt = System.currentTimeMillis() + 8 * 3_600_000L,
                    participantsText = "3 de 5 han participado",
                    alreadySubmitted = false,
                    isOpen = true
                ),
                activity = listOf(
                    HomeActivityItem(
                        id = "proof_001",
                        userName = "María R.",
                        userInitial = "M",
                        action = "Subió su prueba diaria",
                        groupName = "Pandilla",
                        timestamp = "hace 14 min",
                        mediaType = MediaType.VIDEO
                    ),
                    HomeActivityItem(
                        id = "proof_003",
                        userName = "Sara L.",
                        userInitial = "S",
                        action = "Subió su prueba diaria",
                        groupName = "Pandilla",
                        timestamp = "hace 2 h",
                        mediaType = MediaType.PHOTO
                    )
                )
            ),
            onGroupClick = {},
            onChallengeClick = {},
            onGroupsClick = {},
            onCreateGroupClick = {},
            onProfileClick = {},
            onRetry = {}
        )
    }
}
