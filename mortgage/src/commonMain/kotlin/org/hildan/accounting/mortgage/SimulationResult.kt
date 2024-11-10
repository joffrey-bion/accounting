package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

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

    val summarizedYears: List<MortgageYearSummary> = monthlyPayments
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
        .sortedBy { it.year }
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
     * For full years, this is 12 but the first and last years of a mortgage are usually not complete.
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
