package com.example.kadai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.ErrorColor
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextMuted

/**
 * Votos positivos / negativos de una prueba.
 *
 * @param userVote `null` si el usuario no ha votado, `true` positivo, `false` negativo.
 * @param enabled `false` para la propia prueba o cuando la votación está cerrada.
 */
@Composable
fun VoteButtons(
    positiveVotes: Int,
    negativeVotes: Int,
    userVote: Boolean?,
    enabled: Boolean,
    onVote: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VoteChip(
            icon = Icons.Outlined.ThumbUp,
            count = positiveVotes,
            label = "Votos positivos",
            selected = userVote == true,
            accent = PurpleSoft,
            enabled = enabled,
            onClick = { onVote(true) }
        )
        VoteChip(
            icon = Icons.Outlined.ThumbDown,
            count = negativeVotes,
            label = "Votos negativos",
            selected = userVote == false,
            accent = ErrorColor,
            enabled = enabled,
            onClick = { onVote(false) }
        )
    }
}

@Composable
private fun VoteChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    selected: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val container = if (selected) accent.copy(alpha = 0.16f) else SurfaceElevated
    val border = if (selected) accent.copy(alpha = 0.6f) else Border
    val content = if (selected) accent else TextMuted

    Row(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .semantics { contentDescription = "$label: $count" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelMedium,
            color = content
        )
    }
}

/** Reacciones disponibles en una prueba. */
@Composable
fun ReactionBar(
    selected: Set<ReactionType>,
    counts: Map<ReactionType, Int>,
    enabled: Boolean,
    onToggle: (ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReactionType.entries.forEach { reaction ->
            val isSelected = reaction in selected
            val count = counts[reaction] ?: 0
            Row(
                modifier = Modifier
                    .alpha(if (enabled) 1f else 0.5f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) PurplePrimary.copy(alpha = 0.18f) else SurfaceElevated)
                    .border(1.dp, if (isSelected) PurpleSoft else Border, RoundedCornerShape(14.dp))
                    .clickable(enabled = enabled) { onToggle(reaction) }
                    .padding(horizontal = 9.dp, vertical = 5.dp)
                    .semantics { contentDescription = "${reaction.label}: $count" },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(text = reaction.emoji, fontSize = 13.sp)
                if (count > 0) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) PurpleSoft else TextMuted
                    )
                }
            }
        }
    }
}

val ReactionType.emoji: String
    get() = when (this) {
        ReactionType.FIRE -> "🔥"
        ReactionType.CLAP -> "👏"
        ReactionType.ROCKET -> "🚀"
        ReactionType.HEART -> "❤️"
        ReactionType.LAUGH -> "😂"
    }

val ReactionType.label: String
    get() = when (this) {
        ReactionType.FIRE -> "Fuego"
        ReactionType.CLAP -> "Aplauso"
        ReactionType.ROCKET -> "Cohete"
        ReactionType.HEART -> "Me encanta"
        ReactionType.LAUGH -> "Risa"
    }
