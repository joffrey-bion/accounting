package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*

/**
 * A payment towards the mortgage.
 */
data class MortgagePayment(
    /**
     * The date this payment was made.
     */
    val date: LocalDate,
    /**
     * The balance of the mortgage before making this payment.
     */
    val balanceBefore: Amount,
    /**
     * The amount used to pay back the mortgage, which is subtracted from the current mortgage balance as a result.
     */
    val principalReduction: Amount,
    /**
     * The amount invested voluntarily to repay a part of the mortgage this month on top of the mandatory payment.
     * Like [principalReduction], it is subtracted from the current mortgage balance (principal) as a result.
     */
    val extraPrincipalReduction: Amount,
    /**
     * The applicable annual interest rate at the time of this payment.
     */
    val appliedInterestRate: Fraction,
    /**
     * The interest paid to the bank for borrowing the money.
     */
    val interest: Amount,
) {
    /**
     * The total amount paid to the bank.
     */
    val total: Amount = principalReduction + extraPrincipalReduction + interest
}
