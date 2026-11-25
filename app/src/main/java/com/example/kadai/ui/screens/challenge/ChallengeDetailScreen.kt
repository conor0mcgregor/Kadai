package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.TextSecondary
import com.example.kadai.ui.theme.Warning
import com.example.kadai.domain.usecase.formatCountdown
import com.example.kadai.domain.usecase.formatDateTime
import kotlinx.coroutines.delay

@Composable
fun ChallengeDetailRoute(
    dailyChallengeId: String,
    onBack: () -> Unit,
    onSubmitProof: (dailyChallengeId: String) -> Unit,
    onViewGroupProofs: (groupId: String) -> Unit,
    onProposeChallenge: () -> Unit,
    onViewHistory: (groupId: String) -> Unit,
    onViewMyProof: (proofId: String) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: ChallengeDetailViewModel = viewModel(
        key = "challenge-$dailyChallengeId",
        factory = KadaiViewModelFactory(application) { ChallengeDetailViewModel(it, dailyChallengeId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChallengeDetailScreen(
        state = state,
        onBack = onBack,
        onSubmitProof = { onSubmitProof(dailyChallengeId) },
        onViewGroupProofs = onViewGroupProofs,
        onProposeChallenge = onProposeChallenge,
        onViewHistory = onViewHistory,
        onViewMyProof = onViewMyProof,
        onRetry = viewModel::retry
    )
}

@Composable
fun ChallengeDetailScreen(
    state: ChallengeDetailUiState,
    onBack: () -> Unit,
    onSubmitProof: () -> Unit,
    onViewGroupProofs: (String) -> Unit,
    onProposeChallenge: () -> Unit,
    onViewHistory: (String) -> Unit,
    onViewMyProof: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.kadai.ui.theme.Background)
    ) {
        KadaiTopBar(title = "Reto del día", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Cargando el reto…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> ChallengeDetailContent(
                state = state,
                onSubmitProof = onSubmitProof,
                onViewGroupProofs = onViewGroupProofs,
                onProposeChallenge = onProposeChallenge,
                onViewHistory = onViewHistory,
                onViewMyProof = onViewMyProof
            )
        }
    }
}

@Composable
private fun ChallengeDetailContent(
    state: ChallengeDetailUiState,
    onSubmitProof: () -> Unit,
    onViewGroupProofs: (String) -> Unit,
    onProposeChallenge: () -> Unit,
    onViewHistory: (String) -> Unit,
    onViewMyProof: (String) -> Unit
) {
    val ticker by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val isOpen = state.isOpen && state.closesAt > ticker

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            ChallengeHero(state = state, isOpen = isOpen, remainingMillis = state.closesAt - ticker)
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            KadaiCard {
                Text(
                    text = "Cómo funciona",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                InfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Publicado",
                    value = formatDateTime(state.publishedAt)
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Outlined.Timer,
                    label = "Cierra",
                    value = formatDateTime(state.closesAt)
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Outlined.Group,
                    label = "Participación",
                    value = if (state.memberCount == 0) {
                        "Sin grupo activo"
                    } else {
                        "${state.participantsCount} de ${state.memberCount} en ${state.groups.firstOrNull()?.name.orEmpty()}"
                    }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            if (state.alreadySubmitted && state.myProofId != null) {
                KadaiStatusPill(
                    text = "Prueba entregada",
                    color = com.example.kadai.ui.theme.Success
                )
                Spacer(modifier = Modifier.height(10.dp))
                KadaiSecondaryButton(
                    text = "Ver mi prueba",
                    onClick = { onViewMyProof(state.myProofId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            KadaiPrimaryButton(
                text = when {
                    !isOpen -> "Reto cerrado"
                    state.alreadySubmitted -> "Sustituir mi prueba"
                    else -> "Subir mi prueba"
                },
                onClick = onSubmitProof,
                enabled = isOpen
            )
            Spacer(modifier = Modifier.height(10.dp))

            KadaiSecondaryButton(
                text = "Ver pruebas del grupo",
                onClick = { state.primaryGroupId?.let(onViewGroupProofs) },
                enabled = state.primaryGroupId != null
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KadaiSecondaryButton(
                        text = "Proponer reto",
                        onClick = onProposeChallenge
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    KadaiSecondaryButton(
                        text = "Historial",
                        onClick = { state.primaryGroupId?.let(onViewHistory) },
                        enabled = state.primaryGroupId != null
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ChallengeHero(
    state: ChallengeDetailUiState,
    isOpen: Boolean,
    remainingMillis: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF581C87), Color(0xFF7C3AED), Color(0xFF9333EA))
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✦  ${state.category.ifBlank { "RETO GLOBAL" }.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
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
                    contentDescription = "Tiempo restante",
                    tint = Color(0xFFFDE68A),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isOpen) formatCountdown(remainingMillis) else "Cerrado",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFFFDE68A)
                )
            }
        }
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                letterSpacing = (-0.3).sp
            ),
            color = Color.White
        )
        Text(
            text = if (state.alreadySubmitted) {
                "Ya has entregado tu prueba. Puedes sustituirla mientras el reto siga abierto."
            } else {
                "Sube tu foto o vídeo antes de que se cierre el reto."
            },
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = Color.White.copy(alpha = 0.82f)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextDisabled
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun ChallengeDetailPreview() {
    KadaiTheme {
        ChallengeDetailScreen(
            state = ChallengeDetailUiState(
                isLoading = false,
                dailyChallengeId = "daily_001",
                title = "Grita en medio de la calle",
                description = "Sube un vídeo gritando en medio de la calle. Cuanto antes lo hagas, más puntos te llevas.",
                category = "Atrevido",
                publishedAt = System.currentTimeMillis() - 3_600_000L,
                closesAt = System.currentTimeMillis() + 8 * 3_600_000L,
                isOpen = true,
                alreadySubmitted = false,
                groups = listOf(ChallengeGroupOption("group_pandilla", "Pandilla", "🏆")),
                primaryGroupId = "group_pandilla",
                participantsCount = 3,
                memberCount = 5
            ),
            onBack = {},
            onSubmitProof = {},
            onViewGroupProofs = {},
            onProposeChallenge = {},
            onViewHistory = {},
            onViewMyProof = {},
            onRetry = {}
        )
    }
}
