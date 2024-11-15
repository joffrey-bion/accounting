package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.money.pct
import kotlin.test.Test
import kotlin.test.assertEquals

class DayCountConventionTest {

    private val jan01 = LocalDate(2024, 1, 1)
    private val jan06 = LocalDate(2024, 1, 6)
    private val jan16 = LocalDate(2024, 1, 16)
    private val feb01 = LocalDate(2024, 2, 1) // leap year -> 29 days
    private val feb16 = LocalDate(2024, 2, 16)
    private val mar01 = LocalDate(2024, 3, 1)
    private val apr01 = LocalDate(2024, 4, 1)
    private val apr06 = LocalDate(2024, 4, 6)
    private val apr16 = LocalDate(2024, 4, 16)
    private val may01 = LocalDate(2024, 5, 1)

    @Test
    fun fullMonth_actualActual() {
        assertEquals(100.pct, DayCountConvention.ActualActual.monthRatio(jan01, feb01)) // 31 days
        assertEquals(100.pct, DayCountConvention.ActualActual.monthRatio(feb01, mar01)) // 29 days
        assertEquals(100.pct, DayCountConvention.ActualActual.monthRatio(apr01, may01)) // 30 days
    }

    @Test
    fun fullMonth_thirty360() {
        assertEquals(100.pct, DayCountConvention.Thirty360.monthRatio(jan01, feb01)) // 31 days
        assertEquals(100.pct, DayCountConvention.Thirty360.monthRatio(feb01, mar01)) // 29 days
        assertEquals(100.pct, DayCountConvention.Thirty360.monthRatio(apr01, may01)) // 30 days
    }

    @Test
    fun splitOn16thWith31Days_actualActual() {
        assertEquals(Fraction(15, 31), DayCountConvention.ActualActual.monthRatio(jan01, jan16))
        assertEquals(Fraction(16, 31), DayCountConvention.ActualActual.monthRatio(jan16, feb01))
    }

    @Test
    fun splitOn16thWith31Days_thirty360() {
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(jan01, jan16))
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(jan16, feb01))
    }

    @Test
    fun splitOn16thWith30Days_actualActual() {
        assertEquals(50.pct, DayCountConvention.ActualActual.monthRatio(apr01, apr16))
        assertEquals(50.pct, DayCountConvention.ActualActual.monthRatio(apr16, may01))
    }

    @Test
    fun splitOn16thWith30Days_thirty360() {
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(apr01, apr16))
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(apr16, may01))
    }

    @Test
    fun splitOn16thWith29Days_actualActual() {
        assertEquals(Fraction(15, 29), DayCountConvention.ActualActual.monthRatio(feb01, feb16))
        assertEquals(Fraction(14, 29), DayCountConvention.ActualActual.monthRatio(feb16, mar01))
    }

    @Test
    fun splitOn16thWith29Days_thirty360() {
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(feb01, feb16))
        assertEquals(50.pct, DayCountConvention.Thirty360.monthRatio(feb16, mar01))
    }

    @Test
    fun splitOn6thWith30Days_actualActual() {
        assertEquals(Fraction(5, 30), DayCountConvention.ActualActual.monthRatio(apr01, apr06))
        assertEquals(Fraction(25, 30), DayCountConvention.ActualActual.monthRatio(apr06, may01))
    }

    @Test
    fun splitOn6thWith30Days_thirty360() {
        assertEquals(Fraction(5, 30), DayCountConvention.Thirty360.monthRatio(apr01, apr06))
        assertEquals(Fraction(25, 30), DayCountConvention.Thirty360.monthRatio(apr06, may01))
    }

    @Test
    fun splitOn6thWith31Days_actualActual() {
        assertEquals(Fraction(5, 31), DayCountConvention.ActualActual.monthRatio(jan01, jan06))
        assertEquals(Fraction(26, 31), DayCountConvention.ActualActual.monthRatio(jan06, feb01))
    }

    @Test
    fun splitOn6thWith31Days_thirty360() {
        assertEquals(Fraction(5, 30), DayCountConvention.Thirty360.monthRatio(jan01, jan06))
        assertEquals(Fraction(25, 30), DayCountConvention.Thirty360.monthRatio(jan06, feb01))
    }
}
