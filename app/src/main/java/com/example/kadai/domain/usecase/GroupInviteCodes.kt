package com.example.kadai.domain.usecase

import java.text.Normalizer
import java.util.Locale

private const val CODE_LENGTH = 6

/**
 * Normaliza un código de invitación introducido por el usuario.
 * Elimina espacios, acentos y caracteres no alfanuméricos, y lo pasa a mayúsculas.
 */
fun normalizeInviteCode(raw: String): String =
    Normalizer.normalize(raw, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .uppercase(Locale.ROOT)
        .filter { it.isLetterOrDigit() }

/**
 * Genera un código legible a partir del nombre del grupo. Es una regla de
 * presentación provisional: cuando exista backend, el código se persistirá
 * en el grupo y no dependerá del nombre.
 */
fun generateInviteCode(name: String): String {
    val base = normalizeInviteCode(name).take(CODE_LENGTH)
    return base.ifBlank { "KADAI" }
}

/**
 * Garantiza que el código generado no colisione con los códigos ya existentes.
 * Añade un sufijo numérico incremental cuando es necesario.
 */
fun uniqueInviteCode(name: String, existingCodes: Collection<String>): String {
    val base = generateInviteCode(name)
    val used = existingCodes.map(::normalizeInviteCode).toSet()
    if (base !in used) return base

    var suffix = 2
    while (true) {
        val candidate = (base.take(CODE_LENGTH) + suffix)
        if (candidate !in used) return candidate
        suffix++
    }
}
