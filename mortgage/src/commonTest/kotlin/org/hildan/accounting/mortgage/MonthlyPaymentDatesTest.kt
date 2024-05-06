package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import kotlin.test.*

class MonthlyPaymentDatesTest {
    
    @Test
    fun fullFirstMonth() {
        val actualDates = monthlyPaymentDates(
            startDate = LocalDate(2023, 11, 1),
            termInYears = 30,
            dayOfMonth = 28,
        )
        val expectedFirstPayment = LocalDate(2023, 11, 28)
        val expectedDates = generateSequence(expectedFirstPayment) { it.plus(1, DateTimeUnit.MONTH) }
            .take(30 * 12)
            .toList()
        assertEquals(expectedDates, actualDates)
    }

    @Test
    fun partialFirstMonth() {
        val actualDates = monthlyPaymentDates(
            startDate = LocalDate(2023, 11, 20),
            termInYears = 30,
            dayOfMonth = 28,
        )
        val expectedFirstPayment = LocalDate(2023, 11, 28)
        val expectedDates = generateSequence(expectedFirstPayment) { it.plus(1, DateTimeUnit.MONTH) }
            .take(30 * 12 + 1)
            .toList()
        assertEquals(expectedDates, actualDates)
    }
}
