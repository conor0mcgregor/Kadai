package com.example.kadai.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class TimePresentationTest {

    private val now = 1_700_000_000_000L

    @Test
    fun `countdown formats hours minutes and seconds`() {
        assertEquals("08:24:15", formatCountdown(8 * 3_600_000L + 24 * 60_000L + 15_000L))
    }

    @Test
    fun `countdown is closed when no time remains`() {
        assertEquals("Cerrado", formatCountdown(0))
        assertEquals("Cerrado", formatCountdown(-5_000))
    }

    @Test
    fun `relative time shows minutes`() {
        assertEquals("hace 14 min", formatRelativeTime(now - 14 * 60_000L, now))
    }

    @Test
    fun `relative time shows hours and days`() {
        assertEquals("hace 2 h", formatRelativeTime(now - 2 * 3_600_000L, now))
        assertEquals("hace 3 d", formatRelativeTime(now - 3 * 86_400_000L, now))
    }

    @Test
    fun `relative time never shows negative elapsed time`() {
        assertEquals("ahora", formatRelativeTime(now + 10_000L, now))
    }
}
