package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.eur
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedPaymentsTest {

    @Test
    fun paidIn_inclusiveStart_exclusiveEnd_boundaries() {
        val payments = listOf(
            Payment(date = LocalDate(2024, 1, 9), amount = "10".eur, description = "before start"),
            Payment(date = LocalDate(2024, 1, 10), amount = "11".eur, description = "on start"),
            Payment(date = LocalDate(2024, 1, 15), amount = "12".eur, description = "inside"),
            Payment(date = LocalDate(2024, 1, 19), amount = "13".eur, description = "inside last day"),
            Payment(date = LocalDate(2024, 1, 20), amount = "14".eur, description = "on end (excluded)"),
        )
        val sp = SortedPayments(payments)

        val period = PaymentPeriod(
            start = LocalDate(2024, 1, 10),
            endExclusive = LocalDate(2024, 1, 20),
        )

        val expected = listOf(
            payments[1], // 10th
            payments[2], // 15th
            payments[3], // 19th
        )
        assertEquals(expected, sp.paidIn(period))
    }

    @Test
    fun paidIn_sortsInput_unorderedList() {
        val p1 = Payment(LocalDate(2023, 12, 5), "1".eur)
        val p2 = Payment(LocalDate(2023, 12, 1), "2".eur)
        val p3 = Payment(LocalDate(2023, 12, 3), "3".eur)

        // Intentionally unordered input
        val sp = SortedPayments(listOf(p1, p2, p3))

        val period = PaymentPeriod(
            start = LocalDate(2023, 12, 1),
            endExclusive = LocalDate(2023, 12, 31),
        )

        assertEquals(listOf(p2, p3, p1), sp.paidIn(period), "returned payments should be ordered by date")
    }

    @Test
    fun paidIn_emptyWhenNoPaymentsInPeriod() {
        val sp = SortedPayments(
            listOf(
                Payment(LocalDate(2024, 2, 1), "1".eur),
                Payment(LocalDate(2024, 2, 10), "2".eur),
            )
        )
        val period = PaymentPeriod(
            start = LocalDate(2024, 1, 1),
            endExclusive = LocalDate(2024, 1, 31),
        )
        assertTrue(sp.paidIn(period).isEmpty())
    }

    @Test
    fun paidIn_multipleOnSameDate_allIncluded() {
        val sameDay = LocalDate(2024, 3, 15)
        val p1 = Payment(sameDay, "1".eur, "a")
        val p2 = Payment(sameDay, "2".eur, "b")
        val p3 = Payment(sameDay, "3".eur, "c")

        val sp = SortedPayments(listOf(p3, p1, p2))
        val period = PaymentPeriod(
            start = LocalDate(2024, 3, 1),
            endExclusive = LocalDate(2024, 4, 1),
        )

        assertEquals(listOf(p3, p1, p2), sp.paidIn(period))
    }
}
