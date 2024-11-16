package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.mortgage.DayCountConvention.ActualActual
import org.hildan.accounting.mortgage.DayCountConvention.ThirtyE360
import org.hildan.accounting.mortgage.DayCountConvention.ThirtyE360ISDA
import kotlin.test.Test
import kotlin.test.assertEquals

class DayCountConventionTest {

    private fun f(numerator: Int, denominator: Int): Fraction = Fraction(numerator, denominator)

    private fun period(start: String, end: String): PaymentPeriod = PaymentPeriod(
        start = LocalDate.parse(start),
        endExclusive = LocalDate.parse(end),
    )

    @Test
    fun dayCountFactor_fullMonths() {
        assertEquals(f(31, 366), ActualActual.dayCountFactor(period("2024-01-01", "2024-02-01")))
        assertEquals(f(29, 366), ActualActual.dayCountFactor(period("2024-02-01", "2024-03-01")))
        assertEquals(f(30, 366), ActualActual.dayCountFactor(period("2024-04-01", "2024-05-01")))

        assertEquals(f(31, 365), ActualActual.dayCountFactor(period("2023-01-01", "2023-02-01")))
        assertEquals(f(28, 365), ActualActual.dayCountFactor(period("2023-02-01", "2023-03-01")))
        assertEquals(f(30, 365), ActualActual.dayCountFactor(period("2023-04-01", "2023-05-01")))

        assertEquals(f(30, 360), ThirtyE360.dayCountFactor(period("2024-01-01", "2024-02-01")))
        assertEquals(f(30, 360), ThirtyE360.dayCountFactor(period("2024-02-01", "2024-03-01")))
        assertEquals(f(30, 360), ThirtyE360.dayCountFactor(period("2024-04-01", "2024-05-01")))

        assertEquals(f(30, 360), ThirtyE360ISDA.dayCountFactor(period("2024-01-01", "2024-02-01")))
        assertEquals(f(30, 360), ThirtyE360ISDA.dayCountFactor(period("2024-02-01", "2024-03-01")))
        assertEquals(f(30, 360), ThirtyE360ISDA.dayCountFactor(period("2024-04-01", "2024-05-01")))
    }

    @Test
    fun dayCountFactor_partialMonths() {
        assertEquals(f(15, 366), ActualActual.dayCountFactor(period("2024-01-01", "2024-01-16")))
        assertEquals(f(16, 366), ActualActual.dayCountFactor(period("2024-01-16", "2024-02-01")))
        assertEquals(f(15, 366), ActualActual.dayCountFactor(period("2024-02-01", "2024-02-16")))
        assertEquals(f(14, 366), ActualActual.dayCountFactor(period("2024-02-16", "2024-03-01")))
        assertEquals(f(15, 366), ActualActual.dayCountFactor(period("2024-04-01", "2024-04-16")))
        assertEquals(f(15, 366), ActualActual.dayCountFactor(period("2024-04-16", "2024-05-01")))

        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-01-01", "2024-01-16")))
        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-01-16", "2024-02-01")))
        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-02-01", "2024-02-16")))
        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-02-16", "2024-03-01")))
        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-04-01", "2024-04-16")))
        assertEquals(f(15, 360), ThirtyE360.dayCountFactor(period("2024-04-16", "2024-05-01")))

        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-01-01", "2024-01-16")))
        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-01-16", "2024-02-01")))
        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-02-01", "2024-02-16")))
        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-02-16", "2024-03-01")))
        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-04-01", "2024-04-16")))
        assertEquals(f(15, 360), ThirtyE360ISDA.dayCountFactor(period("2024-04-16", "2024-05-01")))
    }

    @Test
    fun dayCountFactor_multipleYears() {
        assertEquals(Fraction(2), ActualActual.dayCountFactor(period("2023-01-01", "2025-01-01")))
        assertEquals(Fraction(2), ThirtyE360.dayCountFactor(period("2023-01-01", "2025-01-01")))
        assertEquals(Fraction(2), ThirtyE360ISDA.dayCountFactor(period("2023-01-01", "2025-01-01")))
    }

    @Test
    fun dayCountFactor_leapYearToNonLeapYear() {
        assertEquals(f(335, 366) + f(31, 365), ActualActual.dayCountFactor(period("2024-02-01", "2025-02-01")))
        assertEquals(f(360, 360), ThirtyE360.dayCountFactor(period("2024-02-01", "2025-02-01")))
        assertEquals(f(360, 360), ThirtyE360ISDA.dayCountFactor(period("2024-02-01", "2025-02-01")))
    }

    @Test
    fun dayCountFactor_nonLeapYearToLeapYear() {
        assertEquals(f(334, 365) + f(31, 366), ActualActual.dayCountFactor(period("2023-02-01", "2024-02-01")))
        assertEquals(f(360, 360), ThirtyE360.dayCountFactor(period("2023-02-01", "2024-02-01")))
        assertEquals(f(360, 360), ThirtyE360ISDA.dayCountFactor(period("2023-02-01", "2024-02-01")))
    }

    @Test
    fun dayCountFactor_boundsOnLastDayOfMonth() {
        assertEquals(f(1, 365), ActualActual.dayCountFactor(period("2023-02-28", "2023-03-01")))
        assertEquals(f(1, 366), ActualActual.dayCountFactor(period("2024-02-29", "2024-03-01")))
        assertEquals(f(1, 365), ActualActual.dayCountFactor(period("2023-04-30", "2023-05-01")))
        assertEquals(f(365 - 30, 365) + f(31, 366), ActualActual.dayCountFactor(period("2023-01-31", "2024-02-01")))

        assertEquals(f(3, 360), ThirtyE360.dayCountFactor(period("2023-02-28", "2023-03-01")))
        assertEquals(f(2, 360), ThirtyE360.dayCountFactor(period("2024-02-29", "2024-03-01")))
        assertEquals(f(1, 360), ThirtyE360.dayCountFactor(period("2023-04-30", "2023-05-01")))
        assertEquals(f(361, 360), ThirtyE360.dayCountFactor(period("2023-01-31", "2024-02-01")))

        assertEquals(f(1, 360), ThirtyE360ISDA.dayCountFactor(period("2023-02-28", "2023-03-01")))
        assertEquals(f(1, 360), ThirtyE360ISDA.dayCountFactor(period("2024-02-29", "2024-03-01")))
        assertEquals(f(1, 360), ThirtyE360ISDA.dayCountFactor(period("2023-04-30", "2023-05-01")))
        assertEquals(f(361, 360), ThirtyE360ISDA.dayCountFactor(period("2023-01-31", "2024-02-01")))
    }
}
