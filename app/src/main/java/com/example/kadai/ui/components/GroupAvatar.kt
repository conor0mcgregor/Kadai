package com.example.kadai.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.kadai.ui.theme.PurplePrimary

/**
 * Foto de perfil de un grupo con recorte redondeado.
 *
 * Acepta URLs normales (Firebase Storage, galería local mediante `content://`)
 * y referencias `res://nombre_drawable` usadas por los datos de ejemplo.
 * Si no hay foto, o falla la carga, muestra la inicial del grupo.
 */
@Composable
fun GroupAvatar(
    name: String,
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    cornerRadius: Dp = 16.dp,
    contentDescription: String? = "Foto de $name"
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(colors = listOf(PurplePrimary, Color(0xFF9333EA)))
            ),
        contentAlignment = Alignment.Center
    ) {
        val reference = photoUrl?.takeIf { it.isNotBlank() }
        when {
            reference == null -> GroupAvatarInitial(name)

            reference.startsWith(RES_PREFIX) -> {
                val context = LocalContext.current
                val drawableId = remember(reference) {
                    context.drawableId(reference.removePrefix(RES_PREFIX))
                }
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(drawableId),
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    GroupAvatarInitial(name)
                }
            }

            else -> SubcomposeAsyncImage(
                model = reference,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { GroupAvatarInitial(name) },
                error = { GroupAvatarInitial(name) }
            )
        }
    }
}

@Composable
private fun GroupAvatarInitial(name: String) {
    Text(
        text = name.trim().firstOrNull()?.uppercase() ?: "?",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        color = Color.White
    )
}

private fun Context.drawableId(name: String): Int =
    resources.getIdentifier(name, "drawable", packageName)

private const val RES_PREFIX = "res://"
