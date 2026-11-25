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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.theme.BorderStrong
import com.example.kadai.ui.theme.Success
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextSecondary

/**
 * Tarjeta protagonista del reto diario (extraída de Home, tarea C-10).
 *
 * @param alreadySubmitted muestra el estado de participación del usuario.
 * @param isOpen `false` cuando el periodo ya se ha cerrado.
 */
@Composable
fun ChallengeCard(
    title: String,
    description: String,
    timeRemaining: String,
    participantsText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    alreadySubmitted: Boolean = false,
    isOpen: Boolean = true,
    category: String? = null,
    actionLabel: String? = null
) {
    val resolvedAction = actionLabel ?: when {
        !isOpen -> "Reto cerrado"
        alreadySubmitted -> "Prueba enviada · Ver"
        else -> "Subir mi prueba"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(1.dp, BorderStrong, RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF581C87),
                            Color(0xFF7C3AED),
                            Color(0xFF9333EA)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .align(Alignment.TopEnd)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "✦  ${(category ?: "RETO GLOBAL").uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "Tiempo restante",
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timeRemaining,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = TextDisabled,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = participantsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDisabled
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = when {
                            !isOpen -> Brush.linearGradient(
                                colors = listOf(Color(0xFF2A2A35), Color(0xFF2A2A35))
                            )
                            alreadySubmitted -> Brush.linearGradient(
                                colors = listOf(Success, Color(0xFF16A34A))
                            )
                            else -> Brush.linearGradient(
                                colors = listOf(Color(0xFF7C3AED), Color(0xFF9333EA))
                            )
                        }
                    )
                    .clickable(enabled = isOpen) { onActionClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resolvedAction,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    color = if (isOpen) Color.White else TextDisabled
                )
            }
        }
    }
}
