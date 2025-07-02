package org.hildan.accounting.mortgage

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Returns the total number of days in the month of this [LocalDate].
 */
internal fun LocalDate.nDaysInMonth(): Int = nextMonthFirstDay().minus(1, DateTimeUnit.DAY).day

/**
 * Returns a [LocalDate] that corresponds to the first day of the next month.
 */
internal fun LocalDate.nextMonthFirstDay(): LocalDate = withDayOfMonth(1).plus(1, DateTimeUnit.MONTH)

/**
 * Returns a copy of this [LocalDate] with the day of month replaced with the given [dayOfMonth], keeping the year and
 * month intact. The given [dayOfMonth] has to be valid for the month of this date.
 */
internal fun LocalDate.withDayOfMonth(dayOfMonth: Int): LocalDate = LocalDate(year, month, dayOfMonth)
