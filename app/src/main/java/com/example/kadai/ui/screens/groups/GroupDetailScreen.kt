package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiSectionHeader
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.MemberRow
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

private val EDIT_EMOJIS = listOf("🏆", "🎯", "⚡", "🎵", "🔥", "🍕", "🎮", "🌊")

@Composable
fun GroupDetailRoute(
    groupId: String,
    onBack: () -> Unit,
    onManageMembers: (groupId: String) -> Unit,
    onInvite: (groupId: String) -> Unit,
    onRanking: (groupId: String) -> Unit,
    onHistory: (groupId: String) -> Unit,
    onGroupProofs: (groupId: String) -> Unit,
    onGroupGone: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: GroupDetailViewModel = viewModel(
        key = "group-detail-$groupId",
        factory = KadaiViewModelFactory(application) { GroupDetailViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.leftGroup, state.deletedGroup) {
        if (state.leftGroup || state.deletedGroup) onGroupGone()
    }

    GroupDetailScreen(
        state = state,
        onBack = onBack,
        onManageMembers = { onManageMembers(groupId) },
        onInvite = { onInvite(groupId) },
        onRanking = { onRanking(groupId) },
        onHistory = { onHistory(groupId) },
        onGroupProofs = { onGroupProofs(groupId) },
        onLeaveGroup = viewModel::leaveGroup,
        onDeleteGroup = viewModel::deleteGroup,
        onUpdateGroup = viewModel::updateGroup,
        onDismissError = viewModel::clearActionError,
        onRetry = viewModel::retry
    )
}

@Composable
fun GroupDetailScreen(
    state: GroupDetailUiState,
    onBack: () -> Unit,
    onManageMembers: () -> Unit,
    onInvite: () -> Unit,
    onRanking: () -> Unit,
    onHistory: () -> Unit,
    onGroupProofs: () -> Unit,
    onLeaveGroup: () -> Unit,
    onDeleteGroup: () -> Unit,
    onUpdateGroup: (String, String, String, () -> Unit) -> Unit,
    onDismissError: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = state.name.ifBlank { "Grupo" }, onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Cargando el grupo…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    GroupHeader(
                        state = state,
                        onEdit = {
                            onDismissError()
                            showEditDialog = true
                        }
                    )
                }

                state.actionError?.let { message ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ErrorColor.copy(alpha = 0.12f))
                                .clickable { onDismissError() }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorColor
                            )
                        }
                    }
                }

                item {
                    KadaiCard {
                        Text(
                            text = "Código de invitación",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextDisabled
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.inviteCode.ifBlank { "—" },
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 3.sp
                                ),
                                color = PurpleSoft,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Compartir",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceElevated)
                                    .clickable { onInvite() }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                item {
                    KadaiSectionHeader(
                        title = "Miembros",
                        badge = "${state.memberCount}",
                        actionLabel = if (state.isAdmin) "Gestionar" else null,
                        onActionClick = onManageMembers
                    )
                }

                items(state.members.take(6), key = { it.userId }) { member ->
                    MemberRow(
                        displayName = member.displayName,
                        initial = member.initial,
                        roleLabel = member.roleLabel,
                        isCurrentUser = member.isCurrentUser,
                        isInactive = member.isInactive
                    )
                }

                item {
                    Text(
                        text = "Acciones del grupo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionRow(
                            icon = Icons.Outlined.PhotoLibrary,
                            label = "Ver pruebas del grupo",
                            onClick = onGroupProofs
                        )
                        ActionRow(
                            icon = Icons.Outlined.Leaderboard,
                            label = "Ranking del grupo",
                            onClick = onRanking
                        )
                        ActionRow(
                            icon = Icons.Outlined.History,
                            label = "Historial de retos",
                            onClick = onHistory
                        )
                        ActionRow(
                            icon = Icons.Outlined.IosShare,
                            label = "Invitar a un amigo",
                            onClick = onInvite
                        )
                        if (state.isAdmin) {
                            ActionRow(
                                icon = Icons.Outlined.Settings,
                                label = "Editar grupo",
                                onClick = { showEditDialog = true }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.isAdmin) {
                        DangerRow(
                            icon = Icons.Outlined.DeleteForever,
                            label = "Eliminar grupo",
                            onClick = { showDeleteDialog = true }
                        )
                    } else {
                        DangerRow(
                            icon = Icons.Outlined.PersonRemove,
                            label = "Abandonar grupo",
                            onClick = { showLeaveDialog = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showLeaveDialog) {
        ConfirmDialog(
            title = "Abandonar grupo",
            message = "Dejarás de participar en ${state.name}. Conservarás tu historial de puntos, pero no podrás votar ni entregar pruebas en este grupo.",
            confirmLabel = "Abandonar",
            onConfirm = {
                showLeaveDialog = false
                onLeaveGroup()
            },
            onDismiss = { showLeaveDialog = false }
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Eliminar grupo",
            message = "Esta acción elimina ${state.name} para todos sus miembros y no se puede deshacer.",
            confirmLabel = "Eliminar",
            onConfirm = {
                showDeleteDialog = false
                onDeleteGroup()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showEditDialog) {
        EditGroupDialog(
            initialName = state.name,
            initialDescription = state.description,
            initialEmoji = state.emoji,
            isWorking = state.isWorking,
            onSave = { name, description, emoji ->
                onUpdateGroup(name, description, emoji) { showEditDialog = false }
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun GroupHeader(state: GroupDetailUiState, onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.emoji, fontSize = 30.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (state.isAdmin) {
                        Spacer(modifier = Modifier.size(8.dp))
                        KadaiStatusPill(text = "Admin", color = PurpleSoft)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (state.description.isBlank()) "Sin descripción" else state.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = TextDisabled,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${state.memberCount} miembros activos",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDisabled
                    )
                }
            }
            if (state.isAdmin) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated)
                        .clickable { onEdit() }
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar grupo",
                        tint = PurpleSoft,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurpleSoft,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun DangerRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ErrorColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ErrorColor,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = ErrorColor
        )
    }
}

@Composable
private fun EditGroupDialog(
    initialName: String,
    initialDescription: String,
    initialEmoji: String,
    isWorking: Boolean,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var emoji by remember { mutableStateOf(initialEmoji) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text(text = "Editar grupo", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                KadaiTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Nombre del grupo",
                    leadingIcon = Icons.Outlined.Group
                )
                KadaiTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Descripción",
                    leadingIcon = Icons.Outlined.Edit
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EDIT_EMOJIS.take(5).forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (option == emoji) PurpleSoft.copy(alpha = 0.2f) else SurfaceElevated)
                                .clickable { emoji = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option, fontSize = 18.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, description, emoji) },
                enabled = !isWorking
            ) {
                Text(text = if (isWorking) "Guardando…" else "Guardar", color = PurpleSoft)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TextMuted)
            }
        }
    )
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text(text = title, color = TextPrimary) },
        text = { Text(text = message, color = TextMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel, color = ErrorColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TextMuted)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun GroupDetailPreview() {
    KadaiTheme {
        GroupDetailScreen(
            state = GroupDetailUiState(
                isLoading = false,
                groupId = "group_pandilla",
                name = "Pandilla",
                description = "El grupo de siempre",
                emoji = "🏆",
                inviteCode = "PANDIL",
                isAdmin = true,
                memberCount = 5,
                members = listOf(
                    GroupMemberItem("user_alex", "Alex García", "A", "Admin", true, false),
                    GroupMemberItem("user_maria", "María R.", "M", "Miembro", false, false),
                    GroupMemberItem("user_carlos", "Carlos M.", "C", "Miembro", false, false),
                    GroupMemberItem("user_jorge", "Jorge P.", "J", "Miembro", false, true)
                )
            ),
            onBack = {},
            onManageMembers = {},
            onInvite = {},
            onRanking = {},
            onHistory = {},
            onGroupProofs = {},
            onLeaveGroup = {},
            onDeleteGroup = {},
            onUpdateGroup = { _, _, _, _ -> },
            onDismissError = {},
            onRetry = {}
        )
    }
}
