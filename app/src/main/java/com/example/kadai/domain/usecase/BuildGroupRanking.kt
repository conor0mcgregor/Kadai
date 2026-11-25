package com.example.kadai.domain.usecase

/**
 * Posición de un miembro en el ranking de un grupo.
 * Los empates comparten posición y el siguiente salta (1, 1, 3).
 */
data class RankingPosition(
    val userId: String,
    val position: Int,
    val points: Int
)

/**
 * Construye el ranking de un grupo a partir de sus miembros y sus puntos.
 *
 * Reglas:
 * - Se ordena de mayor a menor puntuación.
 * - El orden de entrada se usa como desempate estable (por ejemplo, alfabético).
 * - Todos los miembros aparecen, incluso con 0 puntos.
 */
fun buildGroupRanking(
    orderedUserIds: List<String>,
    pointsByUser: Map<String, Int>
): List<RankingPosition> {
    val ordered = orderedUserIds
        .distinct()
        .sortedByDescending { pointsByUser[it] ?: 0 }

    var previousPoints: Int? = null
    var previousPosition = 0

    return ordered.mapIndexed { index, userId ->
        val points = pointsByUser[userId] ?: 0
        val position = if (points == previousPoints) previousPosition else index + 1
        previousPoints = points
        previousPosition = position
        RankingPosition(userId = userId, position = position, points = points)
    }
}
