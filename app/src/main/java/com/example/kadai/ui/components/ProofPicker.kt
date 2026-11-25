package com.example.kadai.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kadai.domain.model.MediaType
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.SurfaceElevated
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

/** Archivo multimedia elegido por el usuario. */
data class PickedMedia(
    val location: String,
    val mediaType: MediaType,
    val displayName: String
)

/**
 * Selector de prueba multimedia compartido.
 *
 * Permite alternar entre foto y vídeo y abrir el selector del sistema. No sube
 * nada todavía: el llamador decide cómo persistir [PickedMedia] en el mock.
 */
@Composable
fun ProofPicker(
    mediaType: MediaType,
    onMediaTypeChange: (MediaType) -> Unit,
    pickedMedia: PickedMedia?,
    onMediaPicked: (PickedMedia) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onMediaPicked(
                PickedMedia(
                    location = uri.toString(),
                    mediaType = mediaType,
                    displayName = queryDisplayName(context, uri) ?: "Archivo seleccionado"
                )
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MediaTypeOption(
                label = "Foto",
                icon = Icons.Outlined.Image,
                selected = mediaType == MediaType.PHOTO,
                onClick = { onMediaTypeChange(MediaType.PHOTO) },
                modifier = Modifier.weight(1f)
            )
            MediaTypeOption(
                label = "Vídeo",
                icon = Icons.Outlined.Videocam,
                selected = mediaType == MediaType.VIDEO,
                onClick = { onMediaTypeChange(MediaType.VIDEO) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (pickedMedia == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, Border, RoundedCornerShape(18.dp))
                    .clickable {
                        picker.launch(
                            PickVisualMediaRequest(
                                if (mediaType == MediaType.PHOTO) {
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                } else {
                                    ActivityResultContracts.PickVisualMedia.VideoOnly
                                }
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (mediaType == MediaType.PHOTO) Icons.Outlined.Image
                        else Icons.Outlined.Videocam,
                        contentDescription = null,
                        tint = PurpleSoft,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (mediaType == MediaType.PHOTO) "Elegir fotografía"
                        else "Elegir vídeo",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Desde la galería del dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth()) {
                ProofMedia(
                    storagePath = pickedMedia.location,
                    mediaType = pickedMedia.mediaType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Quitar archivo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pickedMedia.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "JPG, PNG o MP4 · máximo 20 MB (límite provisional hasta cerrar D-08)",
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled
        )
    }
}

@Composable
private fun MediaTypeOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) PurpleSoft.copy(alpha = 0.14f) else SurfaceElevated)
            .border(
                width = 1.dp,
                color = if (selected) PurpleSoft else Border,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) PurpleSoft else TextDisabled,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) PurpleSoft else TextDisabled
        )
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? =
    runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
    }.getOrNull()
