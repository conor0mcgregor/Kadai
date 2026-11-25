package com.example.kadai.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProofRulesTest {

    @Test
    fun `no votes is valid automatically`() {
        assertTrue(isProofValid(positiveVotes = 0, negativeVotes = 0))
        assertNull(approvalPercentage(0, 0))
    }

    @Test
    fun `three positive and one negative is valid`() {
        assertTrue(isProofValid(positiveVotes = 3, negativeVotes = 1))
        assertEquals(75, approvalPercentage(3, 1))
    }

    @Test
    fun `two positive and two negative reaches the fifty percent threshold`() {
        assertTrue(isProofValid(positiveVotes = 2, negativeVotes = 2))
        assertEquals(50, approvalPercentage(2, 2))
    }

    @Test
    fun `one positive and three negative is not valid`() {
        assertFalse(isProofValid(positiveVotes = 1, negativeVotes = 3))
        assertEquals(25, approvalPercentage(1, 3))
    }

    @Test
    fun `negative inputs are treated as zero votes`() {
        assertTrue(isProofValid(positiveVotes = -2, negativeVotes = 0))
        assertFalse(isProofValid(positiveVotes = 0, negativeVotes = 1))
    }
}
