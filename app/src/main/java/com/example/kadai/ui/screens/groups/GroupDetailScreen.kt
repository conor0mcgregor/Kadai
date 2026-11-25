package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.PersonRemove
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.kadai.ui.components.GroupAvatar
import com.example.kadai.ui.components.KadaiCard
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.components.KadaiSecondaryButton
import com.example.kadai.ui.components.KadaiStatusPill
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.components.KadaiTopBar
import com.example.kadai.ui.components.MemberRow
import com.example.kadai.ui.components.RankingRow
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.BorderStrong
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

private enum class GroupSection(val label: String) {
    RANKING("Ranking"),
    MEMBERS("Miembros")
}

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
    onUpdateGroup: (String, String, String?, () -> Unit) -> Unit,
    onDismissError: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var section by rememberSaveable { mutableStateOf(GroupSection.MEMBERS.name) }
    val selectedSection = GroupSection.valueOf(section)

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    InviteCodeCard(
                        inviteCode = state.inviteCode,
                        onInvite = onInvite
                    )
                }

                item {
                    Text(
                        text = "Acciones del grupo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        HeroActionCard(
                            title = "Ver pruebas del grupo",
                            subtitle = "Revisa, vota y reacciona a las entregas de hoy",
                            icon = Icons.Outlined.PhotoLibrary,
                            onClick = onGroupProofs
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GroupActionCard(
                                icon = Icons.Outlined.IosShare,
                                title = "Invitar amigos",
                                subtitle = "Código: ${state.inviteCode}",
                                onClick = onInvite,
                                modifier = Modifier.weight(1f)
                            )
                            GroupActionCard(
                                icon = Icons.Outlined.History,
                                title = "Historial",
                                subtitle = "Retos pasados",
                                onClick = onHistory,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (state.isAdmin) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                GroupActionCard(
                                    icon = Icons.Outlined.Group,
                                    title = "Gestionar",
                                    subtitle = "Roles y accesos",
                                    onClick = onManageMembers,
                                    modifier = Modifier.weight(1f)
                                )
                                GroupActionCard(
                                    icon = Icons.Outlined.Edit,
                                    title = "Editar grupo",
                                    subtitle = "Nombre y foto",
                                    onClick = {
                                        onDismissError()
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    accentBorder = true
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    GroupSectionTabs(
                        selected = selectedSection,
                        rankingCount = state.ranking.size,
                        memberCount = state.memberCount,
                        onSelect = { section = it.name }
                    )
                }

                when (selectedSection) {
                    GroupSection.RANKING -> {
                        if (state.ranking.isEmpty()) {
                            item {
                                KadaiCard {
                                    Text(
                                        text = "Todavía no hay puntos",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "El ranking se llenará cuando se validen las primeras pruebas del grupo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextDisabled
                                    )
                                }
                            }
                        } else {
                            items(state.ranking, key = { it.userId }) { entry ->
                                RankingRow(
                                    position = entry.position,
                                    displayName = entry.displayName,
                                    initial = entry.initial,
                                    points = entry.points,
                                    isCurrentUser = entry.isCurrentUser,
                                    isInactive = entry.isInactive,
                                    isAdmin = entry.isAdmin
                                )
                            }
                        }

                        item {
                            KadaiSecondaryButton(
                                text = "Abrir ranking completo",
                                onClick = onRanking
                            )
                        }
                    }

                    GroupSection.MEMBERS -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.memberCount} miembros activos",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (state.isAdmin) {
                                    Text(
                                        text = "Gestionar",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PurpleSoft,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PurpleSoft.copy(alpha = 0.14f))
                                            .clickable { onManageMembers() }
                                            .padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        items(state.members, key = { it.userId }) { member ->
                            MemberRow(
                                displayName = member.displayName,
                                initial = member.initial,
                                roleLabel = member.roleLabel,
                                isCurrentUser = member.isCurrentUser,
                                isInactive = member.isInactive
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
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
            initialPhotoUrl = state.photoUrl,
            isWorking = state.isWorking,
            onSave = { name, description, photoUrl ->
                onUpdateGroup(name, description, photoUrl) { showEditDialog = false }
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
            GroupAvatar(
                name = state.name,
                photoUrl = state.photoUrl,
                size = 68.dp,
                cornerRadius = 20.dp
            )
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
                        Spacer(modifier = Modifier.width(8.dp))
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

        if (state.isAdmin) {
            Spacer(modifier = Modifier.height(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat(
                    icon = Icons.Outlined.Leaderboard,
                    value = state.ranking.firstOrNull { it.isCurrentUser }?.position?.let { "#$it" } ?: "—",
                    label = "Tu posición"
                )
                MiniStat(
                    icon = Icons.Outlined.History,
                    value = state.ranking.firstOrNull { it.isCurrentUser }?.points?.toString() ?: "0",
                    label = "Tus puntos"
                )
            }
        }
    }
}

@Composable
private fun MiniStat(icon: ImageVector, value: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurpleSoft,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled
        )
    }
}

@Composable
private fun InviteCodeCard(inviteCode: String, onInvite: () -> Unit) {
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
                text = inviteCode.ifBlank { "—" },
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
                    .background(PurpleSoft.copy(alpha = 0.16f))
                    .clickable { onInvite() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun HeroActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(PurplePrimary, Color(0xFF9333EA))
                )
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun GroupActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentBorder: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated)
            .border(
                width = 1.dp,
                color = if (accentBorder) PurpleSoft.copy(alpha = 0.5f) else BorderStrong,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(PurplePrimary.copy(alpha = 0.25f), Color(0xFF9333EA).copy(alpha = 0.35f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleSoft,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GroupSectionTabs(
    selected: GroupSection,
    rankingCount: Int,
    memberCount: Int,
    onSelect: (GroupSection) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GroupSectionTab(
            icon = Icons.Outlined.Leaderboard,
            label = "Ranking",
            count = rankingCount,
            selected = selected == GroupSection.RANKING,
            onClick = { onSelect(GroupSection.RANKING) },
            modifier = Modifier.weight(1f)
        )
        GroupSectionTab(
            icon = Icons.Outlined.Group,
            label = "Miembros",
            count = memberCount,
            selected = selected == GroupSection.MEMBERS,
            onClick = { onSelect(GroupSection.MEMBERS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GroupSectionTab(
    icon: ImageVector,
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (selected) {
                    Modifier.background(
                        Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                    )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else TextMuted,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label ($count)",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (selected) Color.White else TextMuted
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
    initialPhotoUrl: String?,
    isWorking: Boolean,
    onSave: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var photoUrl by remember { mutableStateOf(initialPhotoUrl) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { photoUrl = it.toString() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text(text = "Editar grupo", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GroupAvatar(
                        name = name.ifBlank { "Grupo" },
                        photoUrl = photoUrl,
                        size = 64.dp,
                        cornerRadius = 18.dp,
                        contentDescription = "Foto del grupo"
                    )
                    Text(
                        text = "Cambiar foto",
                        style = MaterialTheme.typography.labelMedium,
                        color = PurpleSoft,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PurpleSoft.copy(alpha = 0.16f))
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, description, photoUrl) },
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
                photoUrl = "res://group_cover_pandilla",
                inviteCode = "PANDIL",
                isAdmin = true,
                memberCount = 5,
                members = listOf(
                    GroupMemberItem("user_alex", "Alex García", "A", "Admin", true, false),
                    GroupMemberItem("user_maria", "María R.", "M", "Miembro", false, false),
                    GroupMemberItem("user_carlos", "Carlos M.", "C", "Miembro", false, false),
                    GroupMemberItem("user_jorge", "Jorge P.", "J", "Miembro", false, true)
                ),
                ranking = listOf(
                    RankingEntry(1, "user_alex", "Alex García", "A", 440, true, false, true),
                    RankingEntry(2, "user_maria", "María R.", "M", 200, false, false, false),
                    RankingEntry(3, "user_carlos", "Carlos M.", "C", 180, false, false, false),
                    RankingEntry(4, "user_sara", "Sara L.", "S", 160, false, false, false),
                    RankingEntry(5, "user_jorge", "Jorge P.", "J", 0, false, true, false)
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
