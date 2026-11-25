package com.example.kadai.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildGroupRankingTest {

    @Test
    fun `orders members by points including zero scores`() {
        val ranking = buildGroupRanking(
            orderedUserIds = listOf("alex", "maria", "carlos"),
            pointsByUser = mapOf("alex" to 120, "carlos" to 40)
        )

        assertEquals(listOf("alex", "carlos", "maria"), ranking.map { it.userId })
        assertEquals(listOf(1, 2, 3), ranking.map { it.position })
        assertEquals(listOf(120, 40, 0), ranking.map { it.points })
    }

    @Test
    fun `ties share position and the next one skips`() {
        val ranking = buildGroupRanking(
            orderedUserIds = listOf("alex", "maria", "carlos", "sara"),
            pointsByUser = mapOf("alex" to 100, "maria" to 80, "carlos" to 80, "sara" to 10)
        )

        assertEquals(listOf("alex", "maria", "carlos", "sara"), ranking.map { it.userId })
        assertEquals(listOf(1, 2, 2, 4), ranking.map { it.position })
    }

    @Test
    fun `duplicated member ids are ignored`() {
        val ranking = buildGroupRanking(
            orderedUserIds = listOf("alex", "alex", "maria"),
            pointsByUser = mapOf("alex" to 5, "maria" to 5)
        )

        assertEquals(2, ranking.size)
        assertEquals(listOf(1, 1), ranking.map { it.position })
    }
}
