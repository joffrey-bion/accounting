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
    ActualActual("Actual/Actual") {
        override fun monthRatio(start: LocalDate, endExclusive: LocalDate): Fraction =
            Fraction(start.daysUntil(endExclusive), start.nDaysInMonth())
    },
    /**
     * A day-count rule where each month is considered 30 days long, and each year 360 days.
     * If the bound of a date range falls on the 31st of a month, it is replaced with 30 before the calculation.
     */
    Thirty360("30E/360") {
        override fun monthRatio(start: LocalDate, endExclusive: LocalDate): Fraction {
            val d1 = if (start.dayOfMonth == 31) start.withDayOfMonth(30) else start
            val d2 = if (endExclusive.dayOfMonth == 31) endExclusive.withDayOfMonth(30) else endExclusive
            return Fraction(dayCount30(d1, d2), 30) // not divided by 360 because we want a month ratio, not year
        }
    };

    /**
     * Calculates the fraction of the month that the range from [start] to [endExclusive] represents.
     * The reference month is considered to be the month of the [start] date.
     */
    abstract fun monthRatio(start: LocalDate, endExclusive: LocalDate): Fraction
}

private fun dayCount30(from: LocalDate, to: LocalDate): Int =
    360 * (to.year - from.year) + 30 * (to.monthNumber - from.monthNumber) + to.dayOfMonth - from.dayOfMonth