package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * Defines the conditions of a mortgage.
 */
data class Mortgage(
    /**
     * The amount borrowed.
     */
    val amount: Amount,
    /**
     * The annual interest rate. This fraction of the current balance of the mortgage (the amount that has not been
     * repaid yet) will have to be paid every year. It is usually paid monthly (1/12th of the rate).
     */
    val annualInterestRate: InterestRate,
    /**
     * The year and month of the first payment.
     */
    val startMonth: AbsoluteMonth,
    /**
     * The total duration over which the mortgage will be repaid.
     */
    val nYears: Int = 30,
) {
    /**
     * In case of linear mode, the monthly redemption is the same every month for the duration of the mortgage.
     */
    val linearMonthlyRedemption: Amount = amount / nYears / 12
}
