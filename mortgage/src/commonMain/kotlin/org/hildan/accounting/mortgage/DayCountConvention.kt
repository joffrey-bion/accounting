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
        override fun dayCountFactor(period: PaymentPeriod): Fraction {
            if (period.start.year == period.endExclusive.year) {
                return Fraction(period.start.daysUntil(period.endExclusive), nDaysInYear(period.start.year))
            } else {
                val nDaysInStartYear = period.start.daysUntil(LocalDate(period.start.year + 1, 1, 1))
                val nDaysInEndYear = LocalDate(period.endExclusive.year, 1, 1).daysUntil(period.endExclusive)

                val startFraction = Fraction(nDaysInStartYear, nDaysInYear(period.start.year))
                val endFraction = Fraction(nDaysInEndYear, nDaysInYear(period.endExclusive.year))
                val nMiddleYears = period.endExclusive.year - period.start.year - 1
                return startFraction + endFraction + Fraction(nMiddleYears)
            }
        }
    },
    /**
     * A day-count rule where each month is considered 30 days long, and each year 360 days.
     * If the bound of a date range falls on the 31st of a month, it is replaced with 30 before the calculation.
     */
    ThirtyE360("30E/360") {
        override fun dayCountFactor(period: PaymentPeriod): Fraction {
            val d1 = if (period.start.dayOfMonth == 31) 30 else period.start.dayOfMonth
            val d2 = if (period.endExclusive.dayOfMonth == 31) 30 else period.endExclusive.dayOfMonth
            return dayCount30360(
                y1 = period.start.year,
                m1 = period.start.monthNumber,
                d1 = d1,
                y2 = period.endExclusive.year,
                m2 = period.endExclusive.monthNumber,
                d2 = d2
            )
        }
    },
    /**
     * A day-count rule where each month is considered 30 days long, and each year 360 days.
     * If the bound of a date range falls on the 31st of a month, it is replaced with 30 before the calculation.
     */
    ThirtyE360ISDA("30E/360 ISDA") {
        override fun dayCountFactor(period: PaymentPeriod): Fraction {
            val d1 = if (period.start.dayOfMonth == period.start.nDaysInMonth()) 30 else period.start.dayOfMonth
            val d2 = if (period.endExclusive.dayOfMonth == period.endExclusive.nDaysInMonth()) 30 else period.endExclusive.dayOfMonth
            return dayCount30360(
                y1 = period.start.year,
                m1 = period.start.monthNumber,
                d1 = d1,
                y2 = period.endExclusive.year,
                m2 = period.endExclusive.monthNumber,
                d2 = d2
            )
        }
    };

    /**
     * Calculates the fraction of the annual interest rate that should be applied to the given [period].
     */
    abstract fun dayCountFactor(period: PaymentPeriod): Fraction

    /**
     * Calculates the fraction of the annual interest rate that should be applied to a date range from [start] to
     * [endExclusive].
     */
    fun dayCountFactor(start: LocalDate, endExclusive: LocalDate): Fraction =
        dayCountFactor(PaymentPeriod(start, endExclusive))
}

// can't use LocalDate directly because we need to support February 30th
private fun dayCount30360(y1: Int, m1: Int, d1: Int, y2: Int, m2: Int, d2: Int): Fraction =
    Fraction(360 * (y2 - y1) + 30 * (m2 - m1) + d2 - d1, 360)

private fun nDaysInYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

private fun isLeapYear(year: Int): Boolean {
    val prolepticYear: Long = year.toLong()
    return prolepticYear and 3 == 0L && (prolepticYear % 100 != 0L || prolepticYear % 400 == 0L)
}
