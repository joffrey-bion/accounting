package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.interest.*

/**
 * A period for which a payment applies.
 */
data class PaymentPeriod(
    /**
     * The start date of the period (inclusive).
     */
    val start: LocalDate,
    /**
     * The end date of the period (exclusive).
     */
    val endExclusive: LocalDate,
) {
    operator fun contains(date: LocalDate): Boolean = date >= start && date < endExclusive
}

/**
 * A payment towards the mortgage.
 */
data class MortgagePayment(
    /**
     * The breakdown of the payments for each part.
     */
    val partsBreakdown: List<MortgagePartPayment>,
) {
    /**
     * The date this payment was made. All parts are paid on the same date.
     */
    val date: LocalDate = partsBreakdown.first().date
    /**
     * The date range covered by this payment.
     */
    val period: PaymentPeriod = partsBreakdown.first().period
    /**
     * The balance of the total mortgage (including all parts) before making this payment.
     */
    val balanceBefore: Amount = partsBreakdown.sumOf { it.balanceBefore }
    /**
     * The part of the payment that goes towards the mortgage principal (effectively paying back the mortgage).
     * It is subtracted from the current mortgage balance as a result.
     */
    val principalReduction: Amount = partsBreakdown.sumOf { it.principalReduction }
    /**
     * The amount invested voluntarily to repay a part of the mortgage this month on top of the mandatory payment.
     * Like [principalReduction], it is subtracted from the mortgage balance (principal) as a result.
     */
    val extraPrincipalReduction: Amount = partsBreakdown.sumOf { it.extraPrincipalReduction }
    /**
     * The weighted average of the annual interest rate used for each loan part at the time of this payment.
     */
    val averageInterestRateApplied: ApplicableInterestRate = ApplicableInterestRate(
        annualRate = partsBreakdown.sumOf { it.balanceBefore * it.appliedInterestRate.annualRate } / balanceBefore,
        dayCountConvention = partsBreakdown
            .mapTo(mutableSetOf()) { it.appliedInterestRate.dayCountConvention }
            .singleOrNull()
            ?: error("Different day-count conventions for different parts is not supported"),
    )
    /**
     * The interest paid to the bank as a fee for borrowing the money.
     */
    val interest: Amount = partsBreakdown.sumOf { it.interest }
    /**
     * The total amount due to the bank.
     */
    val totalDue: Amount = partsBreakdown.sumOf { it.totalDue }
    /**
     * The total amount paid to the bank, including what is due and the voluntary extra payments.
     */
    val totalWithExtra: Amount = partsBreakdown.sumOf { it.extraPrincipalReduction }
}

/**
 * A payment towards the mortgage part identified by [partId].
 */
data class MortgagePartPayment(
    /**
     * The [MortgagePartId] of the part that this payment concerns.
     */
    val partId: MortgagePartId,
    /**
     * The date this payment was made.
     */
    val date: LocalDate,
    /**
     * The date range covered by this payment.
     */
    val period: PaymentPeriod,
    /**
     * The balance of the mortgage part before making this payment.
     */
    val balanceBefore: Amount,
    /**
     * The part of the payment that goes towards the mortgage principal (effectively paying back the mortgage).
     * It is subtracted from the current mortgage balance as a result.
     */
    val principalReduction: Amount,
    /**
     * The amount invested voluntarily to repay a part of the mortgage this month on top of the mandatory payment.
     * Like [principalReduction], it is subtracted from the balance (principal) of this mortgage part as a result.
     */
    val extraPrincipalReduction: Amount,
    /**
     * The applicable annual interest rate at the time of this payment.
     */
    val appliedInterestRate: ApplicableInterestRate,
    /**
     * The interest paid to the bank as a fee for borrowing the money.
     */
    val interest: Amount,
) {
    /**
     * The total amount due to the bank, always rounded to the cent.
     */
    // the bank does the rounding here; it doesn't round the principal and interest individually
    val totalDue: Amount = (principalReduction + interest).roundedToTheCent()
}
