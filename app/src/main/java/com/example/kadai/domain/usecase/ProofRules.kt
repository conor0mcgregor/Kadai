package com.example.kadai.domain.usecase

/**
 * Reglas de validación comunitaria de una prueba (documento §5.4).
 *
 * Una prueba es válida si al menos el 50 % de los votos emitidos son positivos.
 * Si no se ha emitido ningún voto, se considera válida automáticamente.
 */
const val APPROVAL_THRESHOLD = 0.5

fun isProofValid(positiveVotes: Int, negativeVotes: Int): Boolean {
    val positive = positiveVotes.coerceAtLeast(0)
    val negative = negativeVotes.coerceAtLeast(0)
    val emitted = positive + negative
    if (emitted == 0) return true
    return positive.toDouble() / emitted >= APPROVAL_THRESHOLD
}

/** Porcentaje entero de votos positivos sobre los emitidos, o `null` si no hay votos. */
fun approvalPercentage(positiveVotes: Int, negativeVotes: Int): Int? {
    val positive = positiveVotes.coerceAtLeast(0)
    val negative = negativeVotes.coerceAtLeast(0)
    val emitted = positive + negative
    if (emitted == 0) return null
    return (positive * 100) / emitted
}
