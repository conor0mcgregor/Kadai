package com.example.kadai.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// KADAI siempre usa tema oscuro. No hay tema claro ni dynamic color.
private val KadaiDarkColorScheme = darkColorScheme(
    primary                = PurplePrimary,
    onPrimary              = Color.White,
    primaryContainer       = PurpleContainer,
    onPrimaryContainer     = OnPurpleContainer,
    secondary              = PurpleSoft,
    onSecondary            = Color.White,
    secondaryContainer     = Color(0xFF2D1B6B),
    onSecondaryContainer   = Color(0xFFDDD6FE),
    tertiary               = Warning,
    onTertiary             = Color.White,
    tertiaryContainer      = WarningContainer,
    onTertiaryContainer    = Color(0xFFFEF3C7),
    background             = Background,
    onBackground           = TextPrimary,
    surface                = Surface,
    onSurface              = TextPrimary,
    surfaceVariant         = SurfaceElevated,
    onSurfaceVariant       = TextMuted,
    outline                = Border,
    outlineVariant         = BorderStrong,
    error                  = ErrorColor,
    onError                = Color.White,
    errorContainer         = ErrorContainer,
    onErrorContainer       = Color(0xFFFECACA),
    scrim                  = Color(0xFF000000),
)

@Composable
fun KadaiTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = KadaiDarkColorScheme,
        typography  = KadaiTypography,
        shapes      = KadaiShapes,
        content     = content
    )
}
