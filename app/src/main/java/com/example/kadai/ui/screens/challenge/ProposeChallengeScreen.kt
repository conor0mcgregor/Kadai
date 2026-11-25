package com.example.kadai.ui.screens.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

private val CATEGORIES = listOf("Atrevido", "Creativo", "Social", "Lifestyle", "Naturaleza")

@Composable
fun ProposeChallengeRoute(
    onBack: () -> Unit,
    viewModel: ProposeChallengeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProposeChallengeScreen(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSubmit = viewModel::submit
    )
}

@Composable
fun ProposeChallengeScreen(
    state: ProposeChallengeUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Proponer reto", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (state.submitted) {
                KadaiCard {
                    Text(
                        text = "¡Propuesta enviada!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tu reto pasará a revisión antes de publicarse. Te avisaremos si se aprueba.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDisabled
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                KadaiPrimaryButton(text = "Volver", onClick = onBack)
                return@Column
            }

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = PurpleSoft
                )
                Text(
                    text = "¿Tienes una idea para el próximo reto?",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Las propuestas pasan por revisión antes de convertirse en el reto del día.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(18.dp))

            KadaiTextField(
                value = state.title,
                onValueChange = onTitleChange,
                placeholder = "Título del reto",
                leadingIcon = Icons.Outlined.Title,
                isError = state.titleError != null,
                errorMessage = state.titleError
            )
            Spacer(modifier = Modifier.height(14.dp))

            KadaiTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                placeholder = "Describe qué tienen que hacer los participantes…",
                leadingIcon = Icons.Outlined.Description,
                isError = state.descriptionError != null,
                errorMessage = state.descriptionError,
                singleLine = false,
                height = 140.dp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Category,
                    contentDescription = null,
                    tint = TextDisabled
                )
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            CategoryChips(selected = state.category, onSelect = onCategoryChange)
            state.categoryError?.let { message ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelMedium,
                    color = ErrorColor
                )
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
                text = if (state.isSubmitting) "Enviando…" else "Enviar propuesta",
                onClick = onSubmit,
                loading = state.isSubmitting
            )
            Spacer(modifier = Modifier.height(10.dp))
            KadaiSecondaryButton(text = "Cancelar", onClick = onBack)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CategoryChips(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CATEGORIES.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { category ->
                    val isSelected = category == selected
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else TextDisabled,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PurpleSoft else Surface)
                            .clickable { onSelect(category) }
                            .padding(vertical = 11.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun ProposeChallengePreview() {
    KadaiTheme {
        ProposeChallengeScreen(
            state = ProposeChallengeUiState(
                title = "Canta en el autobús",
                description = "Graba un vídeo cantando una canción en el autobús sin que nadie te pare.",
                category = "Atrevido"
            ),
            onBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onSubmit = {}
        )
    }
}
