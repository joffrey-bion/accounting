package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*

/**
 * Defines the conditions of a mortgage.
 */
data class Mortgage(
    /**
     * The total amount borrowed.
     */
    val amount: Amount,
    /**
     * The annual interest rate. This fraction of the current balance of the mortgage (the amount that has not been
     * repaid yet) will have to be paid every year. It is usually paid monthly (1/12th of the rate).
     */
    val annualInterestRate: InterestRate,
    /**
     * The start date of the loan (when signed at the notary's office).
     * This is the moment when the funds are released and interest starts being due.
     */
    val startDate: LocalDate,
    /**
     * The payments made voluntarily to pay back the loan, usually to reduce the interest and thus the monthly payments.
     */
    val extraPayments: List<Payment> = emptyList(),
    /**
     * The total duration (in years) over which the mortgage will be repaid.
     */
    val termInYears: Int = 30,
) {
    /**
     * The dates of the monthly payments for the duration of the mortgage.
     */
    val monthlyPaymentDates: List<LocalDate> = monthlyPaymentDates(startDate, termInYears, dayOfMonth = 28)
}

/**
 * Returns the monthly payment dates based on the [startDate] of the loan and the [termInYears], assuming a fixed day
 * [dayOfMonth] each month.
 */
fun monthlyPaymentDates(startDate: LocalDate, termInYears: Int, dayOfMonth: Int): List<LocalDate> {
    val firstPayment = LocalDate(startDate.year, startDate.month, dayOfMonth)
    val firstMonthIsPartial = startDate.dayOfMonth > 1
    val redemptionDay = startDate.plus(termInYears, DateTimeUnit.YEAR).let {
        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        if (firstMonthIsPartial) it.plus(1, DateTimeUnit.MONTH) else it
    }
    return generateSequence(firstPayment) { it.plus(1, DateTimeUnit.MONTH) }
        .takeWhile { it <= redemptionDay }
        .toList()
}
