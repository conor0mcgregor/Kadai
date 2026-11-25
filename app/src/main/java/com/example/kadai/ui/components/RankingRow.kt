package com.example.kadai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
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
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary
import com.example.kadai.ui.theme.Warning

/**
 * Fila de ranking reutilizable en la pantalla de ranking y en el detalle del grupo.
 */
@Composable
fun RankingRow(
    position: Int,
    displayName: String,
    initial: String,
    points: Int,
    modifier: Modifier = Modifier,
    isCurrentUser: Boolean = false,
    isInactive: Boolean = false,
    isAdmin: Boolean = false
) {
    val medal = when (position) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCurrentUser) PurpleSoft.copy(alpha = 0.12f) else Surface)
            .border(
                width = 1.dp,
                color = if (isCurrentUser) PurpleSoft.copy(alpha = 0.45f) else Border,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(34.dp),
            contentAlignment = Alignment.Center
        ) {
            if (medal != null) {
                Text(text = medal, fontSize = 20.sp)
            } else {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isCurrentUser) "$displayName (tú)" else displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isInactive) TextMuted else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isAdmin) {
                    Spacer(modifier = Modifier.size(6.dp))
                    KadaiStatusPill(text = "Admin", color = PurpleSoft)
                }
            }
            if (isInactive) {
                Text(
                    text = "Inactivo · conserva sus puntos",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (position == 1) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (isInactive) TextMuted else PurpleSoft
            )
            Text(
                text = " pts",
                style = MaterialTheme.typography.labelSmall,
                color = TextDisabled
            )
        }
    }
}
