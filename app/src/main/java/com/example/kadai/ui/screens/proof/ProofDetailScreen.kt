package com.example.kadai.ui.screens.proof

import android.app.Application
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.ProofMedia
import com.example.kadai.ui.components.ReactionBar
import com.example.kadai.ui.components.VoteButtons
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

@Composable
fun ProofDetailRoute(
    proofId: String,
    groupId: String,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: ProofDetailViewModel = viewModel(
        key = "proof-$proofId-$groupId",
        factory = KadaiViewModelFactory(application) {
            ProofDetailViewModel(it, proofId, groupId)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProofDetailScreen(
        state = state,
        onBack = onBack,
        onVote = viewModel::vote,
        onToggleReaction = viewModel::toggleReaction,
        onToggleFavorite = viewModel::toggleFavorite,
        onRetry = viewModel::retry
    )
}

@Composable
fun ProofDetailScreen(
    state: ProofDetailUiState,
    onBack: () -> Unit,
    onVote: (Boolean) -> Unit,
    onToggleReaction: (ReactionType) -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(
            title = "Prueba",
            onBack = onBack,
            actions = {
                if (!state.isLoading && state.error == null) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Filled.Bookmark
                            else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (state.isFavorite) {
                                "Quitar de favoritos"
                            } else {
                                "Guardar en favoritos"
                            },
                            tint = if (state.isFavorite) PurpleSoft else TextMuted
                        )
                    }
                }
            }
        )

        when {
            state.isLoading -> KadaiLoadingState(message = "Cargando la prueba…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProofMedia(
                        storagePath = state.storagePath,
                        mediaType = state.mediaType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.isOwn) "${state.authorName} (tú)" else state.authorName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${state.challengeTitle} · ${state.groupName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = state.submittedAtText,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDisabled
                        )
                    }
                }

                item {
                    KadaiCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Validación del grupo",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            when {
                                state.isPending -> KadaiStatusPill(text = "Pendiente", color = Warning)
                                state.isValid -> KadaiStatusPill(text = "Válida", color = Success)
                                else -> KadaiStatusPill(text = "Rechazada", color = ErrorColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when {
                                state.isOwn -> "Los votos de tus compañeros decidirán si tu prueba es válida."
                                !state.isOpen -> "La votación está cerrada."
                                else -> "Necesita al menos el 50 % de votos positivos para considerarse válida."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDisabled
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (state.isOwn) {
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
                                positiveVotes = state.positiveVotes,
                                negativeVotes = state.negativeVotes,
                                userVote = state.userVote,
                                enabled = state.isOpen,
                                onVote = onVote
                            )
                        }
                    }
                }

                item {
                    KadaiCard {
                        Text(
                            text = "Reacciones",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ReactionBar(
                            selected = state.myReactions,
                            counts = state.reactions,
                            enabled = true,
                            onToggle = onToggleReaction
                        )
                    }
                }

                item {
                    Text(
                        text = if (state.isFavorite) {
                            "Guardada en tus favoritos."
                        } else {
                            "Guarda esta prueba para volver a verla cuando quieras."
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDisabled
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun ProofDetailPreview() {
    KadaiTheme {
        ProofDetailScreen(
            state = ProofDetailUiState(
                isLoading = false,
                proofId = "proof_001",
                groupId = "group_pandilla",
                groupName = "Pandilla",
                challengeTitle = "Grita en medio de la calle",
                authorName = "María R.",
                authorInitial = "M",
                mediaType = MediaType.VIDEO,
                storagePath = "proofs/daily_001/user_maria.mp4",
                submittedAtText = "hace 14 min",
                isOpen = true,
                positiveVotes = 2,
                negativeVotes = 1,
                reactions = mapOf(ReactionType.FIRE to 1, ReactionType.CLAP to 1),
                myReactions = setOf(ReactionType.FIRE),
                isFavorite = true
            ),
            onBack = {},
            onVote = {},
            onToggleReaction = {},
            onToggleFavorite = {},
            onRetry = {}
        )
    }
}
