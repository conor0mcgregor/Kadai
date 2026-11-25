package com.example.kadai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

/** Tarjeta de grupo reutilizable en listados, detalle y selección de grupo. */
@Composable
fun GroupCard(
    name: String,
    emoji: String,
    description: String,
    memberCount: Int,
    userPoints: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(PurplePrimary, Color(0xFF9333EA))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = TextDisabled,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (memberCount == 1) "1 miembro" else "$memberCount miembros",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDisabled
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = PurpleSoft,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$userPoints pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleSoft
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(20.dp)
        )
    }
}

/** Fila de miembro con rol, estado y acción opcional. */
@Composable
fun MemberRow(
    displayName: String,
    initial: String,
    modifier: Modifier = Modifier,
    roleLabel: String? = null,
    isCurrentUser: Boolean = false,
    isInactive: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (isInactive) {
                        Modifier.background(Color(0xFF262633))
                    } else {
                        Modifier.background(
                            Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = if (isInactive) TextMuted else Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isCurrentUser) "$displayName (tú)" else displayName,
                style = MaterialTheme.typography.titleSmall,
                color = if (isInactive) TextMuted else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isInactive) {
                Text(
                    text = "Inactivo · conserva su historial",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
        }

        if (roleLabel != null) {
            KadaiStatusPill(
                text = roleLabel,
                color = if (roleLabel == "Admin") PurpleSoft else TextMuted
            )
        }
        trailing?.invoke()
    }
}
