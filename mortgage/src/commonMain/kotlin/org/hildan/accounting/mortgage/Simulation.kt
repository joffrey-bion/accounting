package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * Simulates the reimbursement of this mortgage over its duration, according to the given [profile].
 */
fun Mortgage.simulateLinear(profile: Profile): MortgageSimulation {
    val propertyValue = profile.property.wozValue
    val billsPerMonth = profile.property.installments.groupBy({ it.date }, { it.amount })
    val extraRedemptionsPerMonth = profile.extraRedemptions.groupBy({ it.date }, { it.amount })

    val payments = mutableListOf<MortgagePayment>()

    // we subtract the value because it will be added gradually through bills
    var mortgageBalance = amount - propertyValue

    monthsSequence().forEach { month ->
        val billsThisMonth = billsPerMonth[month] ?: emptyList()
        mortgageBalance += billsThisMonth.sum()

        val balanceBefore = mortgageBalance

        val effectiveAnnualRate = annualInterestRate.at(currentLtvRatio = mortgageBalance / propertyValue)
        val interest = mortgageBalance.coerceAtLeast(Amount.ZERO) * effectiveAnnualRate / 12

        val extraRedemptionsThisMonth = extraRedemptionsPerMonth[month] ?: emptyList()
        val extraRedemption = extraRedemptionsThisMonth.sum()
        mortgageBalance -= extraRedemption
        mortgageBalance -= linearMonthlyRedemption

        val payment = MortgagePayment(
            date = month,
            redemption = linearMonthlyRedemption,
            extraRedemption = extraRedemption,
            interest = interest,
            balanceBefore = balanceBefore,
        )
        payments.add(payment)
    }
    return MortgageSimulation(
        mortgage = this,
        ownFunds = propertyValue - amount,
        monthlyPayments = payments,
    )
}

private fun Mortgage.monthsSequence(): Sequence<AbsoluteMonth> {
    val redemptionDay = startMonth.copy(year = startMonth.year + nYears)
    return generateSequence(startMonth) { it.next() }.takeWhile { it != redemptionDay }
}

data class MortgageSimulation(
    /**
     * The mortgage that was simulated.
     */
    val mortgage: Mortgage,
    /**
     * The personal money invested at the start.
     */
    val ownFunds: Amount,
    /**
     * The list of payments made to repay this mortgage.
     */
    val monthlyPayments: List<MortgagePayment>,
) {
    val totalInterest: Amount = monthlyPayments.sumOf { it.interest }
    val totalPayments: Amount = monthlyPayments.sumOf { it.total }

    fun annuitiesDistribution(): Distribution = monthlyPayments.map { it.total }.distribution()

    fun summarizeYears() = monthlyPayments
        .groupingBy { it.date.year }
        .aggregate { year, acc: MortgageYearSummary?, p, _ ->
            if (acc == null) {
                MortgageYearSummary(
                    year = year,
                    nMonths = 1,
                    redemption = p.redemption,
                    extraRedemption = p.extraRedemption,
                    interest = p.interest,
                    balanceBefore = p.balanceBefore,
                )
            } else {
                MortgageYearSummary(
                    year = year,
                    nMonths = acc.nMonths + 1,
                    redemption = acc.redemption + p.redemption,
                    extraRedemption = acc.extraRedemption + p.extraRedemption,
                    interest = acc.interest + p.interest,
                    balanceBefore = acc.balanceBefore,
                )
            }
        }
        .values
}

/**
 * A payment towards the mortgage.
 */
data class MortgagePayment(
    /**
     * The date this payment was made.
     */
    val date: AbsoluteMonth,
    /**
     * The amount used to pay back the mortgage, which is subtracted from the current mortgage balance as a result.
     */
    val redemption: Amount,
    /**
     * The amount invested spontaneously to repay a part of the mortgage this month on top of the usual redemption.
     * Like [redemption], it is subtracted from the current mortgage balance as a result.
     */
    val extraRedemption: Amount,
    /**
     * The interest paid to the bank for borrowing the money.
     */
    val interest: Amount,
    /**
     * The balance of the mortgage before making this payment.
     */
    val balanceBefore: Amount,
) {
    /**
     * The total amount paid to the bank.
     */
    val total: Amount = redemption + extraRedemption + interest
}

/**
 * A summary of what happened with respect to the mortgage during a year.
 */
data class MortgageYearSummary(
    /**
     * The year that this summary summarizes.
     */
    val year: Int,
    /**
     * The number of months during which the mortgage was paid back.
     * For full years, this is 12 but the first and last year of a mortgage are usually not complete.
     */
    val nMonths: Int,
    /**
     * The amount used to pay back the mortgage, which is subtracted from the mortgage balance during this year.
     * This is the redemption that was planned as part of the mortgage contract.
     */
    val redemption: Amount,
    /**
     * The amount invested spontaneously to repay a part of the mortgage this month on top of the usual redemption.
     * Like [redemption], it is subtracted from the mortgage balance during this year.
     */
    val extraRedemption: Amount,
    /**
     * The interest paid to the bank for borrowing the money.
     */
    val interest: Amount,
    /**
     * The balance of the mortgage at the beginning of the year.
     */
    val balanceBefore: Amount,
) {
    val totalPayments: Amount = redemption + extraRedemption + interest

    val avgMonthlyRedemption: Amount = redemption / nMonths
    val avgMonthlyInterest: Amount = interest / nMonths
    val avgMonthlyPayment: Amount = avgMonthlyRedemption + avgMonthlyInterest
}
