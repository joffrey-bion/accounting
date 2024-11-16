package org.hildan.accounting.mortgage

/**
 * Manages a set of [Payment]s and allows to query them for a given period.
 */
internal class SortedPayments(payments: List<Payment>) {

    private var sortedPayments = payments.sortedBy { it.date }

    /**
     * Returns the payments that happened during the given [period].
     */
    fun paidIn(period: PaymentPeriod): List<Payment> = sortedPayments
        .dropWhile { it.date < period.start }
        .takeWhile { it.date < period.endExclusive }
}
