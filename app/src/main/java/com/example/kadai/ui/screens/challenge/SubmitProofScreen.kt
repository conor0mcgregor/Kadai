package com.example.kadai.ui.screens.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.PickedMedia
import com.example.kadai.ui.components.ProofPicker
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

@Composable
fun SubmitProofRoute(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: SubmitProofViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.submitted) {
        if (state.submitted) onSubmitted()
    }

    SubmitProofScreen(
        state = state,
        onBack = onBack,
        onMediaTypeChange = viewModel::onMediaTypeChange,
        onMediaPicked = { picked -> viewModel.onMediaPicked(picked.location, picked.displayName) },
        onClearMedia = viewModel::onClearMedia,
        onSubmit = viewModel::submit,
        onRetry = viewModel::retry
    )
}

@Composable
fun SubmitProofScreen(
    state: SubmitProofUiState,
    onBack: () -> Unit,
    onMediaTypeChange: (com.example.kadai.domain.model.MediaType) -> Unit,
    onMediaPicked: (PickedMedia) -> Unit,
    onClearMedia: () -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = if (state.isReplacing) "Sustituir prueba" else "Subir prueba", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Preparando la entrega…")
            state.loadError != null -> KadaiErrorState(message = state.loadError, onRetry = onRetry)
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = state.challengeTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (state.isOpen) {
                        "Puedes sustituir tu prueba mientras el reto siga abierto."
                    } else {
                        "El reto ya está cerrado: no se admiten nuevas entregas."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.isOpen) TextDisabled else Warning
                )
                Spacer(modifier = Modifier.height(18.dp))

                KadaiCard {
                    ProofPicker(
                        mediaType = state.mediaType,
                        onMediaTypeChange = onMediaTypeChange,
                        pickedMedia = state.pickedLocation?.let { location ->
                            PickedMedia(
                                location = location,
                                mediaType = state.mediaType,
                                displayName = state.pickedName ?: "Archivo seleccionado"
                            )
                        },
                        onMediaPicked = onMediaPicked,
                        onClear = onClearMedia
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                KadaiCard {
                    Text(
                        text = "Se publicará en todos tus grupos",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tus compañeros podrán votarla y reaccionar. Podrás cambiarla mientras el reto siga abierto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }

                state.validationError?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    MessageBanner(text = message, color = ErrorColor)
                }
                state.submitError?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    MessageBanner(text = message, color = ErrorColor)
                }

                Spacer(modifier = Modifier.height(20.dp))

                KadaiPrimaryButton(
                    text = when {
                        state.isSubmitting -> "Enviando…"
                        state.isReplacing -> "Sustituir prueba"
                        else -> "Enviar prueba"
                    },
                    onClick = onSubmit,
                    enabled = state.isOpen,
                    loading = state.isSubmitting
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Los límites definitivos de tamaño y duración están pendientes de la tarea D-08. Por ahora se acepta una foto o un vídeo de la galería.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun MessageBanner(text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun SubmitProofPreview() {
    KadaiTheme {
        SubmitProofScreen(
            state = SubmitProofUiState(
                isLoading = false,
                dailyChallengeId = "daily_001",
                challengeTitle = "Grita en medio de la calle",
                isOpen = true,
                isReplacing = false
            ),
            onBack = {},
            onMediaTypeChange = {},
            onMediaPicked = {},
            onClearMedia = {},
            onSubmit = {},
            onRetry = {}
        )
    }
}
