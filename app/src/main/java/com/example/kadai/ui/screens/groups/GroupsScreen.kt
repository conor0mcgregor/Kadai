package com.example.kadai.ui.screens.groups

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.GroupCard
import com.example.kadai.ui.components.KadaiEmptyState
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

@Composable
fun GroupsRoute(
    onGroupClick: (String) -> Unit,
    onCreateGroupClick: () -> Unit,
    onJoinGroupClick: () -> Unit,
    viewModel: GroupsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GroupsScreen(
        state = state,
        onGroupClick = onGroupClick,
        onCreateGroupClick = onCreateGroupClick,
        onJoinGroupClick = onJoinGroupClick,
        onRetry = viewModel::retry
    )
}

@Composable
fun GroupsScreen(
    state: GroupsUiState,
    onGroupClick: (String) -> Unit,
    onCreateGroupClick: () -> Unit,
    onJoinGroupClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when {
            state.isLoading -> KadaiLoadingState(
                modifier = Modifier.statusBarsPadding(),
                message = "Cargando tus grupos…"
            )
            state.error != null -> KadaiErrorState(
                message = state.error,
                onRetry = onRetry,
                modifier = Modifier.statusBarsPadding()
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = "Mis grupos",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Compite con tus amigos y escala en el ranking.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDisabled
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (state.groups.isEmpty()) {
                    item {
                        KadaiEmptyState(
                            title = "Aún no perteneces a ningún grupo",
                            message = "Crea tu primer grupo o únete con un código de invitación.",
                            icon = Icons.Outlined.GroupAdd,
                            actionLabel = "Crear grupo",
                            onAction = onCreateGroupClick
                        )
                    }
                } else {
                    items(state.groups, key = { it.groupId }) { group ->
                        GroupCard(
                            name = group.name,
                            emoji = group.emoji,
                            description = group.description,
                            memberCount = group.memberCount,
                            userPoints = group.userPoints,
                            onClick = { onGroupClick(group.groupId) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            KadaiPrimaryButton(text = "Crear grupo", onClick = onCreateGroupClick)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            KadaiSecondaryButton(text = "Unirse", onClick = onJoinGroupClick)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12, heightDp = 800)
@Composable
private fun GroupsPreview() {
    KadaiTheme {
        GroupsScreen(
            state = GroupsUiState(
                isLoading = false,
                groups = listOf(
                    GroupListEntry("group_pandilla", "Pandilla", "El grupo de siempre", "🏆", 5, 440),
                    GroupListEntry("group_uni", "Uni 2024", "Compañeros de universidad", "🎯", 3, 145),
                    GroupListEntry("group_curro", "Curro Squad", "Los del trabajo", "⚡", 2, 0)
                )
            ),
            onGroupClick = {},
            onCreateGroupClick = {},
            onJoinGroupClick = {},
            onRetry = {}
        )
    }
}
