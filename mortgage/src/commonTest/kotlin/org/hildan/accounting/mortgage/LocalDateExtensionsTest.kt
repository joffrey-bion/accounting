package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalDateExtensionsTest {

    @Test
    fun nDaysInMonth_regularMonths() {
        assertEquals(31, LocalDate.parse("2023-01-10").nDaysInMonth())
        assertEquals(30, LocalDate.parse("2023-04-01").nDaysInMonth())
        assertEquals(31, LocalDate.parse("2023-12-31").nDaysInMonth())
    }

    @Test
    fun nDaysInMonth_february_regularAndLeap() {
        assertEquals(29, LocalDate.parse("2000-02-01").nDaysInMonth())
        assertEquals(28, LocalDate.parse("2023-02-01").nDaysInMonth())
        assertEquals(29, LocalDate.parse("2024-02-15").nDaysInMonth())
        assertEquals(28, LocalDate.parse("2100-02-15").nDaysInMonth())
        assertEquals(29, LocalDate.parse("2400-02-15").nDaysInMonth())
    }

    @Test
    fun nextMonthFirstDay_basicTransitions() {
        assertEquals(LocalDate.parse("2023-02-01"), LocalDate.parse("2023-01-15").nextMonthFirstDay())
        assertEquals(LocalDate.parse("2023-02-01"), LocalDate.parse("2023-01-30").nextMonthFirstDay())
        assertEquals(LocalDate.parse("2024-01-01"), LocalDate.parse("2023-12-31").nextMonthFirstDay())
    }

    @Test
    fun nextMonthFirstDay_edgeOnMonthEnds() {
        // End of 31‑day month
        assertEquals(LocalDate.parse("2023-05-01"), LocalDate.parse("2023-04-30").nextMonthFirstDay())
        // End of February including leap year
        assertEquals(LocalDate.parse("2024-03-01"), LocalDate.parse("2024-02-29").nextMonthFirstDay())
        assertEquals(LocalDate.parse("2023-03-01"), LocalDate.parse("2023-02-28").nextMonthFirstDay())
    }

    @Test
    fun withDayOfMonth_setsWithinSameMonth() {
        assertEquals(LocalDate.parse("2023-01-05"), LocalDate.parse("2023-01-15").withDayOfMonth(5))
        assertEquals(LocalDate.parse("2024-02-29"), LocalDate.parse("2024-02-01").withDayOfMonth(29))
    }

    @Test
    fun withDayOfMonth_invalidDay_throws() {
        assertFailsWith<IllegalArgumentException> { LocalDate.parse("2023-02-01").withDayOfMonth(29) }
        assertFailsWith<IllegalArgumentException> { LocalDate.parse("2023-04-01").withDayOfMonth(31) }
        assertFailsWith<IllegalArgumentException> { LocalDate.parse("2024-02-01").withDayOfMonth(30) }
    }
}
