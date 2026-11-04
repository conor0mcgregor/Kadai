package com.example.kadai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val KadaiShapes = Shapes(
    // Badges, pills pequeñas
    extraSmall  = RoundedCornerShape(8.dp),
    // Chips, botones secundarios pequeños
    small       = RoundedCornerShape(12.dp),
    // Campos de texto, botones principales
    medium      = RoundedCornerShape(14.dp),
    // Tarjetas estándar, activity cards
    large       = RoundedCornerShape(16.dp),
    // Tarjeta de reto diario, bottom sheet
    extraLarge  = RoundedCornerShape(20.dp)
)
