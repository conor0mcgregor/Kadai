package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.base.KadaiViewModelFactory
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.MemberRow
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

@Composable
fun ManageMembersRoute(
    groupId: String,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: ManageMembersViewModel = viewModel(
        key = "manage-members-$groupId",
        factory = KadaiViewModelFactory(application) { ManageMembersViewModel(it, groupId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    ManageMembersScreen(
        state = state,
        onBack = onBack,
        onRemoveMember = viewModel::removeMember,
        onClearMessages = viewModel::clearMessages,
        onRetry = viewModel::retry
    )
}

@Composable
fun ManageMembersScreen(
    state: ManageMembersUiState,
    onBack: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onClearMessages: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingRemoval by remember { mutableStateOf<ManageMemberItem?>(null) }
    val activeMembers = state.members.filter { !it.isInactive }
    val inactiveMembers = state.members.filter { it.isInactive }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        KadaiTopBar(title = "Miembros", onBack = onBack)

        when {
            state.isLoading -> KadaiLoadingState(message = "Cargando miembros…")
            state.error != null -> KadaiErrorState(message = state.error, onRetry = onRetry)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = state.groupName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.isAdmin) {
                            "Como administrador puedes expulsar a miembros. No puedes modificar sus votos ni puntos."
                        } else {
                            "Solo el administrador del grupo puede gestionar los miembros."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                state.actionMessage?.let { message ->
                    item {
                        Banner(text = message, color = Success, onClick = onClearMessages)
                    }
                }
                state.actionError?.let { message ->
                    item {
                        Banner(text = message, color = ErrorColor, onClick = onClearMessages)
                    }
                }

                item {
                    SectionLabel(text = "Activos · ${activeMembers.size}")
                }
                items(activeMembers, key = { it.userId }) { member ->
                    MemberRow(
                        displayName = member.displayName,
                        initial = member.initial,
                        roleLabel = member.roleLabel,
                        isCurrentUser = member.isCurrentUser,
                        isInactive = false,
                        trailing = if (member.canRemove) {
                            {
                                IconButton(onClick = { pendingRemoval = member }) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonRemove,
                                        contentDescription = "Expulsar a ${member.displayName}",
                                        tint = ErrorColor,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        } else null
                    )
                }

                if (inactiveMembers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        SectionLabel(text = "Inactivos · ${inactiveMembers.size}")
                    }
                    items(inactiveMembers, key = { it.userId }) { member ->
                        MemberRow(
                            displayName = member.displayName,
                            initial = member.initial,
                            roleLabel = member.roleLabel,
                            isCurrentUser = member.isCurrentUser,
                            isInactive = true
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    pendingRemoval?.let { member ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            containerColor = Surface,
            title = { Text(text = "Expulsar miembro", color = TextPrimary) },
            text = {
                Text(
                    text = "${member.displayName} pasará a inactivo y conservará su historial de puntos. No podrá participar hasta que vuelva a unirse.",
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveMember(member.userId)
                        pendingRemoval = null
                    }
                ) {
                    Text(text = "Expulsar", color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) {
                    Text(text = "Cancelar", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = TextMuted,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun Banner(text: String, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F12)
@Composable
private fun ManageMembersPreview() {
    KadaiTheme {
        ManageMembersScreen(
            state = ManageMembersUiState(
                isLoading = false,
                groupId = "group_pandilla",
                groupName = "Pandilla",
                isAdmin = true,
                members = listOf(
                    ManageMemberItem("user_alex", "Alex García", "A", "Admin", true, false, false),
                    ManageMemberItem("user_maria", "María R.", "M", "Miembro", false, false, true),
                    ManageMemberItem("user_carlos", "Carlos M.", "C", "Miembro", false, false, true),
                    ManageMemberItem("user_jorge", "Jorge P.", "J", "Miembro", false, true, false)
                )
            ),
            onBack = {},
            onRemoveMember = {},
            onClearMessages = {},
            onRetry = {}
        )
    }
}
