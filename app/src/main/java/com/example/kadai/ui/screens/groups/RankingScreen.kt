package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.GroupAvatar
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiEmptyState
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.RankingRow
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            state.groups.forEach { group ->
                                val selected = group.groupId == state.selectedGroupId
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (selected) PurpleSoft else Surface)
                                        .clickable { onSelectGroup(group.groupId) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    GroupAvatar(
                                        name = group.name,
                                        photoUrl = group.photoUrl,
                                        size = 22.dp,
                                        cornerRadius = 7.dp,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else TextMuted
                                    )
                                }
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
                        RankingRow(
                            position = entry.position,
                            displayName = entry.displayName,
                            initial = entry.initial,
                            points = entry.points,
                            isCurrentUser = entry.isCurrentUser,
                            isInactive = entry.isInactive,
                            isAdmin = entry.isAdmin
                        )
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

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun RankingPreview() {
    KadaiTheme {
        RankingScreen(
            state = RankingUiState(
                isLoading = false,
                groups = listOf(
                    RankingGroupOption("group_pandilla", "Pandilla", "res://group_cover_pandilla"),
                    RankingGroupOption("group_uni", "Uni 2024", "res://group_cover_uni")
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
