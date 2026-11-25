package com.example.kadai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kadai.domain.model.MediaType
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextMuted
import com.example.kadai.ui.theme.TextPrimary

private const val LOCAL_URI_PREFIX = "content://"
private const val FILE_URI_PREFIX = "file://"
private const val HTTP_PREFIX = "http"

/**
 * Representación visual de una prueba.
 *
 * Los mocks guardan rutas de Storage que todavía no son URLs públicas, por lo que
 * se muestra un marcador elegante en lugar de una imagen rota. Las imágenes locales
 * elegidas desde el selector sí se previsualizan.
 */
@Composable
fun ProofMedia(
    storagePath: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val isLocalImage = mediaType == MediaType.PHOTO &&
        (storagePath.startsWith(LOCAL_URI_PREFIX) || storagePath.startsWith(FILE_URI_PREFIX) ||
            storagePath.startsWith(HTTP_PREFIX))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceElevated),
        contentAlignment = Alignment.Center
    ) {
        if (isLocalImage) {
            AsyncImage(
                model = storagePath,
                contentDescription = "Fotografía de la prueba",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            PlaceholderMedia(mediaType = mediaType, storagePath = storagePath)
        }
    }
}

@Composable
private fun PlaceholderMedia(mediaType: MediaType, storagePath: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E1B4B), PurplePrimary.copy(alpha = 0.55f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                imageVector = if (mediaType == MediaType.PHOTO) Icons.Outlined.Image
                else Icons.Outlined.Videocam,
                contentDescription = if (mediaType == MediaType.PHOTO) "Fotografía" else "Vídeo",
                tint = TextPrimary,
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (mediaType == MediaType.PHOTO) "Fotografía de la prueba"
                else "Vídeo de la prueba",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = storagePath.substringAfterLast('/'),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Miniatura compacta de archivo para listas. */
@Composable
fun ProofThumbnail(
    storagePath: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F766E))
                )
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (mediaType == MediaType.PHOTO) Icons.Outlined.Image
            else Icons.Outlined.Videocam,
            contentDescription = if (mediaType == MediaType.PHOTO) "Fotografía" else "Vídeo",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
