package org.hildan.accounting.money

import kotlin.test.*

class DistributionTest {

    @Test
    fun singleAmount() {
        val expected = Distribution(42.eur, 42.eur, 42.eur, 42.eur)
        assertEquals(expected, listOf(42.eur).distribution())
    }

    @Test
    fun oneHundred() {
        val amounts = (1..100).map { it.eur }
        val expected = Distribution(100.eur, 99.eur, 95.eur, 90.eur)
        assertEquals(expected, amounts.distribution())
    }
}
