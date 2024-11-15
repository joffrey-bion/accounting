package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate

/**
 * Manages a set of [Payment]s and allows to consume them until a given date using [popPaymentsUntil].
 */
internal class SortedPayments(payments: List<Payment>) {

    private var sortedPayments = payments.sortedBy { it.date }

    /**
     * Removes and returns the list of all payments that occurred until the given [dateExclusive].
     */
    fun popPaymentsUntil(dateExclusive: LocalDate): List<Payment> {
        val bills = sortedPayments.takeWhile { it.date < dateExclusive }
        if (bills.isNotEmpty()) {
            sortedPayments = sortedPayments.drop(bills.size)
        }
        return bills
    }
}
