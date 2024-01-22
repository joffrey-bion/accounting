package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * Settings for a simulation.
 */
data class SimulationSettings(
    /**
     * A convenient name to refer to this simulation.
     */
    val simulationName: String,
    /**
     * The mortgage settings for this simulation.
     */
    val mortgage: Mortgage,
    /**
     * Information about the property being bought.
     */
    val property: Property,
)

/**
 * Runs this simulation assuming a linear mortgage type.
 */
fun SimulationSettings.simulateLinear(): SimulationResult {
    val billsPerMonth = property.installments.groupBy({ it.date }, { it.amount })
    val extraPaymentsPerMonth = mortgage.extraPayments.groupBy({ it.date }, { it.amount })
    val payments = mutableListOf<MortgagePayment>()

    val totalPrice = property.installments.sumOf { it.amount }
    // we subtract the value because it will be added gradually through bills
    var mortgageBalance = mortgage.amount - totalPrice
    mortgage.monthsSequence().forEach { month ->
        val billsThisMonth = billsPerMonth[month] ?: emptyList()
        mortgageBalance += billsThisMonth.sum()

        val balanceBefore = mortgageBalance

        val currentLtvRatio = mortgageBalance / property.wozValue
        val effectiveAnnualRate = mortgage.annualInterestRate.at(month, currentLtvRatio = currentLtvRatio)
        val interest = mortgageBalance.coerceAtLeast(Amount.ZERO) * effectiveAnnualRate / 12

        val extraRedemptionsThisMonth = extraPaymentsPerMonth[month] ?: emptyList()
        val extraRedemption = extraRedemptionsThisMonth.sum()
        mortgageBalance -= extraRedemption
        mortgageBalance -= mortgage.linearMonthlyPrincipalReduction

        val payment = MortgagePayment(
            date = month,
            principalReduction = mortgage.linearMonthlyPrincipalReduction,
            extraPrincipalReduction = extraRedemption,
            interest = interest,
            balanceBefore = balanceBefore,
        )
        payments.add(payment)
    }
    return SimulationResult(settings = this, monthlyPayments = payments)
}

private fun Mortgage.monthsSequence(): Sequence<AbsoluteMonth> {
    val redemptionDay = startMonth.copy(year = startMonth.year + termInYears)
    return generateSequence(startMonth) { it.next() }.takeWhile { it != redemptionDay }
}

data class SimulationResult(
    /**
     * The settings of this simulation.
     */
    val settings: SimulationSettings,
    /**
     * The list of payments made to repay the mortgage.
     */
    val monthlyPayments: List<MortgagePayment>,
) {
    /**
     * The name of this simulation, for easier visualization.
     */
    val name: String get() = settings.simulationName
    /**
     * The amount borrowed from the bank.
     */
    val mortgageAmount: Amount get() = settings.mortgage.amount
    /**
     * The personal money invested at the start.
     */
    val ownFunds: Amount = settings.property.wozValue - mortgageAmount

    val totalInterest: Amount = monthlyPayments.sumOf { it.interest }

    val totalPayments: Amount = monthlyPayments.sumOf { it.total }

    val annuitiesDistribution: Distribution = monthlyPayments.map { it.total }.distribution()

    fun summarizeYears() = monthlyPayments
        .groupingBy { it.date.year }
        .aggregate { year, acc: MortgageYearSummary?, p, _ ->
            if (acc == null) {
                MortgageYearSummary(
                    year = year,
                    nMonths = 1,
                    principalReduction = p.principalReduction,
                    extraPrincipalReduction = p.extraPrincipalReduction,
                    interest = p.interest,
                    balanceBefore = p.balanceBefore,
                )
            } else {
                MortgageYearSummary(
                    year = year,
                    nMonths = acc.nMonths + 1,
                    principalReduction = acc.principalReduction + p.principalReduction,
                    extraPrincipalReduction = acc.extraPrincipalReduction + p.extraPrincipalReduction,
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
    val principalReduction: Amount,
    /**
     * The amount invested voluntarily to repay a part of the mortgage this month on top of the mandatory payment.
     * Like [principalReduction], it is subtracted from the current mortgage balance (principal) as a result.
     */
    val extraPrincipalReduction: Amount,
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
    val total: Amount = principalReduction + extraPrincipalReduction + interest
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
    val principalReduction: Amount,
    /**
     * The amount invested voluntarily to repay a part of the mortgage this month on top of the usual redemption.
     * Like [principalReduction], it is subtracted from the mortgage balance (principal) during this year.
     */
    val extraPrincipalReduction: Amount,
    /**
     * The interest paid to the bank for borrowing the money.
     */
    val interest: Amount,
    /**
     * The balance of the mortgage (principal) at the beginning of the year.
     */
    val balanceBefore: Amount,
) {
    val totalPayments: Amount = principalReduction + extraPrincipalReduction + interest

    val avgMonthlyPrincipalReduction: Amount = principalReduction / nMonths
    val avgMonthlyInterest: Amount = interest / nMonths
    val avgMonthlyPayment: Amount = avgMonthlyPrincipalReduction + avgMonthlyInterest
}
