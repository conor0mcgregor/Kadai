package com.example.kadai.ui.screens.home

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.kadai.ui.components.GroupAvatar
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiPrimaryButton
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
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onGroupClick = onGroupClick,
        onChallengeClick = onChallengeClick,
        onGroupsClick = onGroupsClick,
        onCreateGroupClick = onCreateGroupClick,
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
    now: Long
) {
    val ticker by produceState(initialValue = now) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val challenge = state.challenge
    var expandedGroupId by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
    ) {
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
                HomeGroupsList(
                    groups = state.groups,
                    expandedGroupId = expandedGroupId,
                    onGroupToggle = { groupId ->
                        expandedGroupId = if (expandedGroupId == groupId) null else groupId
                    },
                    onOpenGroup = onGroupClick
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
private fun HomeGroupsList(
    groups: List<HomeGroupItem>,
    expandedGroupId: String?,
    onGroupToggle: (String) -> Unit,
    onOpenGroup: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groups.forEach { group ->
            HomeGroupCard(
                group = group,
                expanded = group.id == expandedGroupId,
                onToggle = { onGroupToggle(group.id) },
                onOpenGroup = { onOpenGroup(group.id) }
            )
        }
    }
}

@Composable
private fun HomeGroupCard(
    group: HomeGroupItem,
    expanded: Boolean,
    onToggle: () -> Unit,
    onOpenGroup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .border(
                width = if (expanded) 1.5.dp else 1.dp,
                color = if (expanded) PurpleSoft.copy(alpha = 0.55f) else Border,
                shape = RoundedCornerShape(18.dp)
            )
            .animateContentSize()
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GroupAvatar(
                name = group.name,
                photoUrl = group.photoUrl,
                size = 54.dp,
                cornerRadius = 16.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (group.description.isNotBlank()) {
                    Text(
                        text = group.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (group.memberCount == 1) "1 miembro" else "${group.memberCount} miembros",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = if (expanded) "Ocultar ranking" else "Ver top 3 del ranking",
                tint = TextDisabled,
                modifier = Modifier.size(22.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Top 3 del ranking",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = PurpleSoft
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (group.topRanking.isEmpty()) {
                Text(
                    text = "Todavía no hay puntos en este grupo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDisabled
                )
            } else {
                group.topRanking.forEachIndexed { index, item ->
                    HomeRankingRow(position = index + 1, item = item)
                    if (index != group.topRanking.lastIndex) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            KadaiPrimaryButton(text = "Ver grupo", onClick = onOpenGroup)
        }
    }
}

@Composable
private fun HomeRankingRow(position: Int, item: HomeRankingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = when (position) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> position.toString()
            },
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.size(width = 24.dp, height = 24.dp)
        )
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.initial,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
        Text(
            text = item.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${item.points} pts",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = PurpleSoft
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
                groups = listOf(
                    HomeGroupItem(
                        id = "group_pandilla",
                        name = "Pandilla",
                        photoUrl = "res://group_cover_pandilla",
                        description = "El grupo de siempre",
                        memberCount = 5,
                        topRanking = listOf(
                            HomeRankingItem("user_alex", "Alex García", "A", 440),
                            HomeRankingItem("user_maria", "María R.", "M", 200),
                            HomeRankingItem("user_carlos", "Carlos M.", "C", 180)
                        )
                    ),
                    HomeGroupItem(
                        id = "group_uni",
                        name = "Uni 2024",
                        photoUrl = "res://group_cover_uni",
                        description = "Compañeros de universidad",
                        memberCount = 3,
                        topRanking = listOf(
                            HomeRankingItem("user_carlos", "Carlos M.", "C", 145),
                            HomeRankingItem("user_alex", "Alex García", "A", 90)
                        )
                    ),
                    HomeGroupItem(
                        id = "group_curro",
                        name = "Curro Squad",
                        photoUrl = "res://group_cover_curro",
                        description = "Los del trabajo",
                        memberCount = 2,
                        topRanking = emptyList()
                    )
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
            onRetry = {}
        )
    }
}
