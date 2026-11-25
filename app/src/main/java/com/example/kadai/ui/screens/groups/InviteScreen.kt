package com.example.kadai.ui.screens.groups

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.GroupAvatar
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

@Composable
fun InviteRoute(
    groupId: String,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: InviteViewModel = viewModel(
        key = "invite-$groupId",
        factory = KadaiViewModelFactory(application) { InviteViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    InviteScreen(state = state, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
fun InviteScreen(
    state: InviteUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var feedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Invitar al grupo", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Preparando la invitación…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupAvatar(
                    name = state.groupName,
                    photoUrl = state.photoUrl,
                    size = 88.dp,
                    cornerRadius = 26.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = state.groupName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Comparte este código para que tus amigos se unan al grupo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDisabled,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(18.dp))
                        .padding(vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CÓDIGO DE INVITACIÓN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextDisabled
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.inviteCode.ifBlank { "—" },
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp
                        ),
                        color = PurpleSoft
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                KadaiPrimaryButton(
                    text = "Copiar código",
                    onClick = {
                        clipboard.setText(AnnotatedString(state.inviteCode))
                        feedback = "Código copiado al portapapeles."
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                KadaiSecondaryButton(
                    text = "Compartir enlace",
                    onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Únete a mi grupo \"${state.groupName}\" en KADAI con el código ${state.inviteCode}."
                            )
                        }
                        context.startActivity(Intent.createChooser(share, "Compartir invitación"))
                    }
                )

                feedback?.let { message ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Success,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                KadaiCard {
                    Text(
                        text = "¿Cómo se unen?",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "En la pantalla de Grupos, pulsa \"Unirse\", introduce este código y confirma. El código no distingue mayúsculas ni espacios.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun InvitePreview() {
    KadaiTheme {
        InviteScreen(
            state = InviteUiState(
                isLoading = false,
                groupId = "group_pandilla",
                groupName = "Pandilla",
                photoUrl = "res://group_cover_pandilla",
                inviteCode = "PANDIL"
            ),
            onBack = {},
            onRetry = {}
        )
    }
}
