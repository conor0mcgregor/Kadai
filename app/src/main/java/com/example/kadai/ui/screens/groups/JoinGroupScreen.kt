package com.example.kadai.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.GroupCard
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

@Composable
fun JoinGroupRoute(
    onBack: () -> Unit,
    onJoined: (groupId: String) -> Unit,
    viewModel: JoinGroupViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.joinedGroupId) {
        state.joinedGroupId?.let(onJoined)
    }

    JoinGroupScreen(
        state = state,
        onBack = onBack,
        onCodeChange = viewModel::onCodeChange,
        onSearch = viewModel::search,
        onJoin = viewModel::join
    )
}

@Composable
fun JoinGroupScreen(
    state: JoinGroupUiState,
    onBack: () -> Unit,
    onCodeChange: (String) -> Unit,
    onSearch: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Unirse a un grupo", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Introduce el código",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pídele a un miembro del grupo su código de invitación. No distingue mayúsculas ni espacios.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(18.dp))

            KadaiTextField(
                value = state.code,
                onValueChange = onCodeChange,
                placeholder = "Código de invitación",
                leadingIcon = Icons.Outlined.Key,
                isError = state.codeError != null,
                errorMessage = state.codeError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )
            Spacer(modifier = Modifier.height(14.dp))

            KadaiPrimaryButton(
                text = if (state.isSearching) "Buscando…" else "Buscar grupo",
                onClick = onSearch,
                loading = state.isSearching
            )

            state.found?.let { found ->
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Hemos encontrado este grupo",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                GroupCard(
                    name = found.name,
                    emoji = found.emoji,
                    description = found.description,
                    memberCount = found.memberCount,
                    userPoints = 0,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                state.joinError?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                KadaiPrimaryButton(
                    text = if (state.isJoining) "Uniéndome…" else "Unirme a ${found.name}",
                    onClick = onJoin,
                    loading = state.isJoining
                )
            }

            if (state.found == null) {
                state.joinError?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            KadaiSecondaryButton(text = "Cancelar", onClick = onBack)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun JoinGroupPreview() {
    KadaiTheme {
        JoinGroupScreen(
            state = JoinGroupUiState(
                code = "PANDIL",
                found = JoinGroupFound(
                    groupId = "group_pandilla",
                    name = "Pandilla",
                    description = "El grupo de siempre",
                    emoji = "🏆",
                    memberCount = 5
                )
            ),
            onBack = {},
            onCodeChange = {},
            onSearch = {},
            onJoin = {}
        )
    }
}
