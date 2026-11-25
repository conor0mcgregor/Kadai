package com.example.kadai.domain.usecase

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val SECOND_MS = 1_000L
private const val MINUTE_MS = 60 * SECOND_MS
private const val HOUR_MS = 60 * MINUTE_MS
private const val DAY_MS = 24 * HOUR_MS

/** Cuenta atrás en formato `HH:MM:SS`. Devuelve `Cerrado` cuando ya no queda tiempo. */
fun formatCountdown(remainingMillis: Long): String {
    if (remainingMillis <= 0L) return "Cerrado"
    val totalSeconds = remainingMillis / SECOND_MS
    val hours = totalSeconds / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(Locale.ROOT, hours, minutes, seconds)
}

/** Texto relativo para actividad reciente: `ahora`, `hace 14 min`, `hace 2 h`, `hace 3 d`… */
fun formatRelativeTime(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val elapsed = (now - timestamp).coerceAtLeast(0L)
    return when {
        elapsed < MINUTE_MS -> "ahora"
        elapsed < HOUR_MS -> "hace ${elapsed / MINUTE_MS} min"
        elapsed < DAY_MS -> "hace ${elapsed / HOUR_MS} h"
        elapsed < 7 * DAY_MS -> "hace ${elapsed / DAY_MS} d"
        else -> formatShortDate(timestamp)
    }
}

/** Fecha y hora legible, por ejemplo `23 nov 2026 · 18:42`. */
fun formatDateTime(timestamp: Long): String =
    SimpleDateFormat("d MMM yyyy · HH:mm", Locale.getDefault()).format(Date(timestamp))

/** Fecha corta, por ejemplo `23 nov 2026`. */
fun formatShortDate(timestamp: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(timestamp))

/** Hora oficial de publicación, por ejemplo `18:42`. */
fun formatTimeOfDay(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
