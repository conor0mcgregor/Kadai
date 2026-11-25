package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
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
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

@Composable
fun ChallengeHistoryRoute(
    groupId: String,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: ChallengeHistoryViewModel = viewModel(
        key = "history-$groupId",
        factory = KadaiViewModelFactory(application) { ChallengeHistoryViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChallengeHistoryScreen(state = state, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
fun ChallengeHistoryScreen(
    state: ChallengeHistoryUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Historial", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Recuperando retos pasados…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            state.items.isEmpty() -> KadaiEmptyState(
                title = "Todavía no hay historial",
                message = "Cuando se cierre tu primer reto aparecerá aquí con su desglose de puntos.",
                icon = Icons.Outlined.CalendarMonth
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = state.groupName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Retos cerrados con tu resultado y el desglose de puntos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                items(state.items, key = { it.dailyChallengeId }) { item ->
                    HistoryCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: ChallengeHistoryItem) {
    KadaiCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.dateText} · ${item.category.ifBlank { "Reto" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
            if (item.hasValidation) {
                KadaiStatusPill(
                    text = if (item.isValid) "Válida" else "Rechazada",
                    color = if (item.isValid) Success else ErrorColor
                )
            } else {
                KadaiStatusPill(text = "Sin validar", color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Participación",
                style = MaterialTheme.typography.labelSmall,
                color = TextDisabled
            )
            Text(
                text = "${item.participantsCount} de ${item.memberCount}",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val breakdown = item.myBreakdown
        if (breakdown == null) {
            Text(
                text = "No participaste en este reto.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreChip(label = "Completar", value = breakdown.completionPoints)
                ScoreChip(label = "Posición", value = breakdown.positionPoints)
                ScoreChip(label = "Rapidez", value = breakdown.timePoints)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Total del reto",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${breakdown.totalPoints} pts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = PurpleSoft
                )
            }
        }
    }
}

@Composable
private fun ScoreChip(label: String, value: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled
        )
        Text(
            text = "+$value",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun ChallengeHistoryPreview() {
    KadaiTheme {
        ChallengeHistoryScreen(
            state = ChallengeHistoryUiState(
                isLoading = false,
                groupId = "group_pandilla",
                groupName = "Pandilla",
                items = listOf(
                    ChallengeHistoryItem(
                        dailyChallengeId = "daily_prev1",
                        title = "Desayuno saludable",
                        category = "Lifestyle",
                        dateText = "19 nov 2026",
                        participantsCount = 4,
                        memberCount = 5,
                        myBreakdown = HistoryScoreBreakdown(100, 50, 70, 220),
                        hasValidation = true,
                        isValid = true
                    ),
                    ChallengeHistoryItem(
                        dailyChallengeId = "daily_prev2",
                        title = "Selfie al amanecer",
                        category = "Naturaleza",
                        dateText = "18 nov 2026",
                        participantsCount = 2,
                        memberCount = 5,
                        myBreakdown = null,
                        hasValidation = false,
                        isValid = false
                    )
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}
