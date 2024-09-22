package org.hildan.accounting.mortgage

import kotlin.test.*

class AbsoluteMonthTest {
    private val jan2023 = AbsoluteMonth(2023, 1)
    private val feb2023 = AbsoluteMonth(2023, 2)
    private val mar2023 = AbsoluteMonth(2023, 3)
    private val aug2023 = AbsoluteMonth(2023, 8)
    private val dec2023 = AbsoluteMonth(2023, 12)
    private val jan2024 = AbsoluteMonth(2024, 1)
    private val apr2024 = AbsoluteMonth(2024, 4)
    private val jul2026 = AbsoluteMonth(2026, 7)

    @Test
    fun next_shouldAddMonth() {
        assertEquals(feb2023, jan2023.next())
        assertEquals(mar2023, feb2023.next())
    }

    @Test
    fun next_shouldIncreaseYearInDecember() {
        assertEquals(jan2024, dec2023.next())
    }

    @Test
    fun plusMonths_shouldAddNMonths() {
        assertEquals(feb2023, jan2023.plusMonths(1))
        assertEquals(mar2023, feb2023.plusMonths(1))
        assertEquals(mar2023, jan2023.plusMonths(2))
        assertEquals(aug2023, mar2023.plusMonths(5))
    }

    @Test
    fun plusMonths_shouldIncreaseYearWhenPassingDecember() {
        assertEquals(jan2024, dec2023.plusMonths(1))
        assertEquals(apr2024, aug2023.plusMonths(8))
    }

    @Test
    fun plusMonths_shouldIncreaseMultipleYearsIfNeeded() {
        assertEquals(jul2026, aug2023.plusMonths(35))
    }
}
