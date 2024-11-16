package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import kotlin.test.*

class MonthlyPaymentPeriodsTest {

    @Test
    fun fullFirstMonth() {
        val actualDates = monthlyPaymentPeriods(startDate = LocalDate(2023, 11, 1), termInYears = 30)
        val expectedFirstPeriod = PaymentPeriod(LocalDate(2023, 11, 1), LocalDate(2023, 12, 1))
        val expectedDates = generateSequence(expectedFirstPeriod) {
            PaymentPeriod(it.endExclusive, it.endExclusive.plus(1, DateTimeUnit.MONTH))
        }
            .take(30 * 12)
            .toList()
        assertEquals(expectedDates, actualDates)
    }

    @Test
    fun partialFirstMonth() {
        val actualDates = monthlyPaymentPeriods(startDate = LocalDate(2023, 11, 20), termInYears = 30)
        val expectedFirstPeriod = PaymentPeriod(LocalDate(2023, 11, 20), LocalDate(2023, 12, 1))
        val expectedDates = generateSequence(expectedFirstPeriod) {
            PaymentPeriod(it.endExclusive, it.endExclusive.plus(1, DateTimeUnit.MONTH))
        }
            .take(30 * 12 + 1)
            .toList()
        assertEquals(expectedDates, actualDates)
    }
}
