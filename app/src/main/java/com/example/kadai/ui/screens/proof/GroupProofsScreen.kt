package com.example.kadai.ui.screens.proof

import android.app.Application
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiEmptyState
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.ProofThumbnail
import com.example.kadai.ui.components.ReactionBar
import com.example.kadai.ui.components.VoteButtons
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

@Composable
fun GroupProofsRoute(
    groupId: String,
    onBack: () -> Unit,
    onOpenProof: (groupId: String, proofId: String) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: GroupProofsViewModel = viewModel(
        key = "group-proofs-$groupId",
        factory = KadaiViewModelFactory(application) { GroupProofsViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    GroupProofsScreen(
        state = state,
        onBack = onBack,
        onFilterChange = viewModel::setFilter,
        onOpenProof = { proofId -> onOpenProof(groupId, proofId) },
        onVote = viewModel::vote,
        onToggleReaction = viewModel::toggleReaction,
        onRetry = viewModel::retry
    )
}

@Composable
fun GroupProofsScreen(
    state: GroupProofsUiState,
    onBack: () -> Unit,
    onFilterChange: (ProofFilter) -> Unit,
    onOpenProof: (String) -> Unit,
    onVote: (GroupProofItem, Boolean) -> Unit,
    onToggleReaction: (String, ReactionType) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = state.groupName.ifBlank { "Pruebas" }, onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Buscando las pruebas…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = state.challengeTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.isOpen) {
                            "Vota las pruebas de tus compañeros. La tuya no puedes votarla."
                        } else {
                            "El reto está cerrado: las votaciones ya no admiten cambios."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.isOpen) TextDisabled else Warning
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    FilterRow(selected = state.filter, onFilterChange = onFilterChange)
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (state.items.isEmpty()) {
                    KadaiEmptyState(
                        title = if (state.filter == ProofFilter.ALL) "Nadie ha entregado aún" else "Sin resultados",
                        message = if (state.filter == ProofFilter.ALL) {
                            "Cuando alguien del grupo suba su prueba aparecerá aquí."
                        } else {
                            "No hay pruebas con este filtro. Prueba con otro estado."
                        }
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.proofId }) { item ->
                            GroupProofCard(
                                item = item,
                                votingEnabled = state.isOpen,
                                onOpen = { onOpenProof(item.proofId) },
                                onVote = { positive -> onVote(item, positive) },
                                onToggleReaction = { type -> onToggleReaction(item.proofId, type) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: ProofFilter,
    onFilterChange: (ProofFilter) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProofFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            Text(
                text = filter.label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else TextMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PurpleSoft else Surface)
                    .clickable { onFilterChange(filter) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun GroupProofCard(
    item: GroupProofItem,
    votingEnabled: Boolean,
    onOpen: () -> Unit,
    onVote: (Boolean) -> Unit,
    onToggleReaction: (ReactionType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .clickable { onOpen() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProofThumbnail(storagePath = item.storagePath, mediaType = item.mediaType)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (item.isOwn) "${item.authorName} (tú)" else item.authorName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.submittedAtText} · ${if (item.mediaType == MediaType.PHOTO) "Foto" else "Vídeo"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
            when {
                item.isPending -> KadaiStatusPill(text = "Pendiente", color = Warning)
                item.isValid -> KadaiStatusPill(text = "Válida", color = Success)
                else -> KadaiStatusPill(text = "Rechazada", color = com.example.kadai.ui.theme.ErrorColor)
            }
        }

        if (item.isOwn) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = TextDisabled,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "No puedes votar tu propia prueba",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
        } else {
            VoteButtons(
                positiveVotes = item.positiveVotes,
                negativeVotes = item.negativeVotes,
                userVote = item.userVote,
                enabled = votingEnabled,
                onVote = onVote
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionBar(
                selected = item.myReactions,
                counts = item.reactions,
                enabled = true,
                onToggle = onToggleReaction
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Ver",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = PurpleSoft,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpen() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun GroupProofsPreview() {
    KadaiTheme {
        GroupProofsScreen(
            state = GroupProofsUiState(
                isLoading = false,
                groupId = "group_pandilla",
                groupName = "Pandilla",
                groupEmoji = "🏆",
                challengeTitle = "Grita en medio de la calle",
                isOpen = true,
                items = listOf(
                    GroupProofItem(
                        proofId = "proof_001",
                        authorName = "María R.",
                        authorInitial = "M",
                        mediaType = MediaType.VIDEO,
                        storagePath = "proofs/daily_001/user_maria.mp4",
                        submittedAtText = "hace 14 min",
                        positiveVotes = 2,
                        negativeVotes = 1,
                        userVote = null,
                        isOwn = false,
                        isPending = true,
                        isValid = false,
                        reactions = mapOf(ReactionType.FIRE to 1, ReactionType.CLAP to 1),
                        myReactions = emptySet()
                    ),
                    GroupProofItem(
                        proofId = "proof_003",
                        authorName = "Sara L.",
                        authorInitial = "S",
                        mediaType = MediaType.PHOTO,
                        storagePath = "proofs/daily_001/user_sara.jpg",
                        submittedAtText = "hace 2 h",
                        positiveVotes = 1,
                        negativeVotes = 1,
                        userVote = true,
                        isOwn = false,
                        isPending = false,
                        isValid = true,
                        reactions = emptyMap(),
                        myReactions = setOf(ReactionType.FIRE)
                    )
                ),
                memberCount = 5
            ),
            onBack = {},
            onFilterChange = {},
            onOpenProof = {},
            onVote = { _, _ -> },
            onToggleReaction = { _, _ -> },
            onRetry = {}
        )
    }
}
