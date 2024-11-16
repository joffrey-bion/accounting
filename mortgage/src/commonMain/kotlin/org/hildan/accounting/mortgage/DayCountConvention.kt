package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.hildan.accounting.money.Fraction

/**
 * Determines how we calculate interest on partial periods.
 *
 * See [Day count convention](https://en.wikipedia.org/wiki/Day_count_convention).
 */
enum class DayCountConvention(val ruleCode: String) {
    /**
     * A day-count rule where the number of days in each month and year are respected.
     */
    ActualActual("Actual/Actual ISDA") {
        override fun dayCountFactor(start: LocalDate, endExclusive: LocalDate): Fraction {
            if (start.year == endExclusive.year) {
                return Fraction(start.daysUntil(endExclusive), nDaysInYear(start.year))
            } else {
                val nDaysInStartYear = start.daysUntil(LocalDate(start.year + 1, 1, 1))
                val nDaysInEndYear = LocalDate(endExclusive.year, 1, 1).daysUntil(endExclusive)

                val startFraction = Fraction(nDaysInStartYear, nDaysInYear(start.year))
                val endFraction = Fraction(nDaysInEndYear, nDaysInYear(endExclusive.year))
                val nMiddleYears = endExclusive.year - start.year - 1
                return startFraction + endFraction + Fraction(nMiddleYears)
            }
        }
    },
    /**
     * A day-count rule where each month is considered 30 days long, and each year 360 days.
     * If the bound of a date range falls on the 31st of a month, it is replaced with 30 before the calculation.
     */
    ThirtyE360("30E/360") {
        override fun dayCountFactor(start: LocalDate, endExclusive: LocalDate): Fraction {
            val d1 = if (start.dayOfMonth == 31) 30 else start.dayOfMonth
            val d2 = if (endExclusive.dayOfMonth == 31) 30 else endExclusive.dayOfMonth
            return dayCount30360(start.year, start.monthNumber, d1, endExclusive.year, endExclusive.monthNumber, d2)
        }
    },
    /**
     * A day-count rule where each month is considered 30 days long, and each year 360 days.
     * If the bound of a date range falls on the 31st of a month, it is replaced with 30 before the calculation.
     */
    ThirtyE360ISDA("30E/360 ISDA") {
        override fun dayCountFactor(start: LocalDate, endExclusive: LocalDate): Fraction {
            val d1 = if (start.dayOfMonth == start.nDaysInMonth()) 30 else start.dayOfMonth
            val d2 = if (endExclusive.dayOfMonth == endExclusive.nDaysInMonth()) 30 else endExclusive.dayOfMonth
            return dayCount30360(start.year, start.monthNumber, d1, endExclusive.year, endExclusive.monthNumber, d2)
        }
    };

    /**
     * Calculates the fraction of the annual interest rate that should be applied to a date range from [start] to
     * [endExclusive].
     */
    abstract fun dayCountFactor(start: LocalDate, endExclusive: LocalDate): Fraction
}

// can't use LocalDate directly because we need to support February 30th
private fun dayCount30360(y1: Int, m1: Int, d1: Int, y2: Int, m2: Int, d2: Int): Fraction =
    Fraction(360 * (y2 - y1) + 30 * (m2 - m1) + d2 - d1, 360)

private fun nDaysInYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

private fun isLeapYear(year: Int): Boolean {
    val prolepticYear: Long = year.toLong()
    return prolepticYear and 3 == 0L && (prolepticYear % 100 != 0L || prolepticYear % 400 == 0L)
}