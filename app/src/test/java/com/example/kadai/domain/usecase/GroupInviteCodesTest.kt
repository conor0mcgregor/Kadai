package com.example.kadai.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInviteCodesTest {

    @Test
    fun `normalizes separators case and accents`() {
        assertEquals("PANDILLA", normalizeInviteCode(" pand-illa "))
        assertEquals("MUSICOS", normalizeInviteCode("Músicos"))
    }

    @Test
    fun `generates a short readable code from the name`() {
        assertEquals("PANDIL", generateInviteCode("Pandilla"))
        assertEquals("MUSICO", generateInviteCode("Músicos"))
    }

    @Test
    fun `blank names fall back to a generic code`() {
        assertEquals("KADAI", generateInviteCode("   "))
    }

    @Test
    fun `avoids collisions with existing codes`() {
        assertEquals("LOSDEL2", uniqueInviteCode("Los del bar", listOf("LOSDEL")))
        assertEquals("LOSDEL", uniqueInviteCode("Los del bar", listOf("OTRO")))
    }
}
