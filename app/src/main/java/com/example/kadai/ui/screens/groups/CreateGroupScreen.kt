package com.example.kadai.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

private val GROUP_EMOJIS = listOf("🏆", "🎯", "⚡", "🎵", "🔥", "🍕", "🎮", "🌊")

@Composable
fun CreateGroupRoute(
    onBack: () -> Unit,
    onCreated: (groupId: String) -> Unit,
    viewModel: CreateGroupViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.createdGroupId) {
        state.createdGroupId?.let(onCreated)
    }

    CreateGroupScreen(
        state = state,
        onBack = onBack,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onEmojiChange = viewModel::onEmojiChange,
        onSubmit = viewModel::submit
    )
}

@Composable
fun CreateGroupScreen(
    state: CreateGroupUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Crear grupo", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Dale personalidad a tu grupo",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Elige un emoji, un nombre y una descripción breve. Después podrás invitar a quien quieras.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Emoji del grupo",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GROUP_EMOJIS.forEach { emoji ->
                    val selected = emoji == state.emoji
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) PurpleSoft.copy(alpha = 0.18f) else Surface)
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) PurpleSoft else Border,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onEmojiChange(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            KadaiTextField(
                value = state.name,
                onValueChange = onNameChange,
                placeholder = "Nombre del grupo",
                leadingIcon = Icons.Outlined.Group,
                isError = state.nameError != null,
                errorMessage = state.nameError
            )
            Spacer(modifier = Modifier.height(14.dp))

            KadaiTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                placeholder = "Descripción breve (opcional)",
                leadingIcon = Icons.Outlined.Description,
                isError = state.descriptionError != null,
                errorMessage = state.descriptionError
            )
            Spacer(modifier = Modifier.height(18.dp))

            KadaiCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.emoji, fontSize = 24.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.name.ifBlank { "Tu nuevo grupo" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = state.description.ifBlank { "Sin descripción" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDisabled
                        )
                    }
                }
            }

            state.submitError?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorColor
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            KadaiPrimaryButton(
                text = if (state.isSubmitting) "Creando…" else "Crear grupo",
                onClick = onSubmit,
                loading = state.isSubmitting
            )
            Spacer(modifier = Modifier.height(10.dp))
            KadaiSecondaryButton(text = "Cancelar", onClick = onBack)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun CreateGroupPreview() {
    KadaiTheme {
        CreateGroupScreen(
            state = CreateGroupUiState(
                name = "Pandilla",
                description = "El grupo de siempre",
                emoji = "🏆"
            ),
            onBack = {},
            onNameChange = {},
            onDescriptionChange = {},
            onEmojiChange = {},
            onSubmit = {}
        )
    }
}
