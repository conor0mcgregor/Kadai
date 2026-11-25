package com.example.kadai.ui.screens.groups

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.GroupAvatar
import com.example.kadai.ui.components.KadaiCard
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
        onPhotoChange = viewModel::onPhotoChange,
        onSubmit = viewModel::submit
    )
}

@Composable
fun CreateGroupScreen(
    state: CreateGroupUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPhotoChange: (String?) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onPhotoChange(uri?.toString())
    }
    val pickPhoto = {
        photoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

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
                text = "Añade una foto, un nombre y una descripción breve. Después podrás invitar a quien quieras.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GroupAvatar(
                    name = state.name.ifBlank { "Grupo" },
                    photoUrl = state.photoUri,
                    size = 84.dp,
                    cornerRadius = 24.dp,
                    contentDescription = "Foto del grupo"
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (state.photoUri == null) "Sin foto todavía" else "Foto lista",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    KadaiSecondaryButton(
                        text = if (state.photoUri == null) "Elegir foto" else "Cambiar foto",
                        onClick = pickPhoto
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

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
                    GroupAvatar(
                        name = state.name.ifBlank { "Grupo" },
                        photoUrl = state.photoUri,
                        size = 52.dp,
                        cornerRadius = 16.dp,
                        contentDescription = null
                    )
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
                photoUri = "res://group_cover_pandilla"
            ),
            onBack = {},
            onNameChange = {},
            onDescriptionChange = {},
            onPhotoChange = {},
            onSubmit = {}
        )
    }
}
