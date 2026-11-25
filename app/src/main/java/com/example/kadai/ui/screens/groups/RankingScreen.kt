package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiEmptyState
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

@Composable
fun RankingRoute(
    groupId: String,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: RankingViewModel = viewModel(
        key = "ranking-$groupId",
        factory = KadaiViewModelFactory(application) { RankingViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    RankingScreen(
        state = state,
        onBack = onBack,
        onSelectGroup = viewModel::selectGroup,
        onRetry = viewModel::retry
    )
}

@Composable
fun RankingScreen(
    state: RankingUiState,
    onBack: () -> Unit,
    onSelectGroup: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Ranking", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Calculando el ranking…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = state.groupName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ranking acumulativo del grupo. Los miembros inactivos conservan sus puntos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.groups.size > 1) {
                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.groups.forEach { group ->
                                val selected = group.groupId == state.selectedGroupId
                                Text(
                                    text = "${group.emoji} ${group.name}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Color.White else TextMuted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (selected) PurpleSoft else Surface)
                                        .clickable { onSelectGroup(group.groupId) }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (state.entries.isEmpty()) {
                    item {
                        KadaiEmptyState(
                            title = "Ranking vacío",
                            message = "Cuando se cierre el primer reto aparecerán aquí los puntos del grupo."
                        )
                    }
                } else {
                    items(state.entries, key = { it.userId }) { entry ->
                        RankingRowCard(entry = entry)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    KadaiCard {
                        Text(
                            text = "¿Cómo se calculan los puntos?",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cada reto válido suma 100 puntos por completarlo, una bonificación por posición de entrega (10 × (miembros − posición)) y hasta 100 puntos por rapidez. Solo las pruebas válidas puntúan.",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                            color = TextDisabled
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun RankingRowCard(entry: RankingEntry) {
    val medal = when (entry.position) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (entry.isCurrentUser) PurpleSoft.copy(alpha = 0.12f) else Surface)
            .border(
                width = 1.dp,
                color = if (entry.isCurrentUser) PurpleSoft.copy(alpha = 0.45f) else Border,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(34.dp),
            contentAlignment = Alignment.Center
        ) {
            if (medal != null) {
                Text(text = medal, fontSize = 20.sp)
            } else {
                Text(
                    text = "${entry.position}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (entry.isInactive) {
                        Modifier.background(Color(0xFF262633))
                    } else {
                        Modifier.background(
                            Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initial,
                style = MaterialTheme.typography.titleSmall,
                color = if (entry.isInactive) TextMuted else Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (entry.isCurrentUser) "${entry.displayName} (tú)" else entry.displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (entry.isInactive) TextMuted else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (entry.isAdmin) {
                    Spacer(modifier = Modifier.size(6.dp))
                    KadaiStatusPill(text = "Admin", color = PurpleSoft)
                }
            }
            if (entry.isInactive) {
                Text(
                    text = "Inactivo · conserva sus puntos",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (entry.position == 1) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = "${entry.points}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (entry.isInactive) TextMuted else PurpleSoft
            )
            Text(
                text = " pts",
                style = MaterialTheme.typography.labelSmall,
                color = TextDisabled
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun RankingPreview() {
    KadaiTheme {
        RankingScreen(
            state = RankingUiState(
                isLoading = false,
                groups = listOf(
                    RankingGroupOption("group_pandilla", "Pandilla", "🏆"),
                    RankingGroupOption("group_uni", "Uni 2024", "🎯")
                ),
                selectedGroupId = "group_pandilla",
                groupName = "Pandilla",
                entries = listOf(
                    RankingEntry(1, "user_alex", "Alex García", "A", 440, true, false, true),
                    RankingEntry(2, "user_maria", "María R.", "M", 200, false, false, false),
                    RankingEntry(3, "user_carlos", "Carlos M.", "C", 180, false, false, false),
                    RankingEntry(4, "user_sara", "Sara L.", "S", 160, false, false, false),
                    RankingEntry(5, "user_jorge", "Jorge P.", "J", 0, false, true, false)
                )
            ),
            onBack = {},
            onSelectGroup = {},
            onRetry = {}
        )
    }
}
