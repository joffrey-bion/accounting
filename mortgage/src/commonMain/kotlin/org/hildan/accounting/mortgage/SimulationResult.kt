package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*

data class SimulationResult(
    /**
     * The settings of this simulation.
     */
    val settings: SimulationSettings,
    /**
     * The list of payments made to repay the mortgage.
     */
    val monthSummaries: List<MortgageMonthSummary>,
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
    val ownFunds: Amount = settings.property.totalPrice - mortgageAmount

    val totalInterest: Amount = monthSummaries.sumOf { it.mortgagePayment.interest }

    val totalPayments: Amount = monthSummaries.sumOf { it.effectiveTotal }

    val annuitiesDistribution: Distribution = monthSummaries.map { it.effectiveTotal }.distribution()

    val yearSummaries: List<MortgageYearSummary> = monthSummaries
        .groupBy { it.date.year }
        .mapValues { (year, summaries) ->
            MortgageYearSummary(
                year = year,
                nMonths = summaries.size,
                balanceBefore = summaries.first().mortgagePayment.balanceBefore,
                principalReduction = summaries.sumOf { it.mortgagePayment.principalReduction },
                extraPrincipalReduction = summaries.sumOf { it.mortgagePayment.extraPrincipalReduction },
                interestRates = summaries.map { it.mortgagePayment.appliedInterestRate }.distinct(),
                interest = summaries.sumOf { it.mortgagePayment.interest },
                constructionAccount = summaries.mapNotNull { it.constructionAccount }
                    .takeIf { it.isNotEmpty() }
                    ?.aggregate(),
            )
        }
        .values
        .sortedBy { it.year }
}

private fun List<ConstructionAccountSummary>.aggregate() = ConstructionAccountSummary(
    balanceBefore = first().balanceBefore,
    paidBills = flatMap { it.paidBills },
    generatedInterest = sumOf { it.generatedInterest },
    deductedInterest = sumOf { it.deductedInterest },
)

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
     * The balance of the mortgage (principal) at the beginning of the year.
     */
    val balanceBefore: Amount,
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
    val interestRates: List<Fraction>,
    /**
     * The interest paid to the bank for borrowing the money.
     */
    val interest: Amount,
    /**
     * The state of the construction account for this year, or null if there is no construction account.
     */
    val constructionAccount: ConstructionAccountSummary?,
) {
    val totalPayments: Amount = principalReduction + extraPrincipalReduction + interest - (constructionAccount?.deductedInterest ?: Amount.ZERO)

    val avgMonthlyPrincipalReduction: Amount = principalReduction / nMonths
    val avgMonthlyInterest: Amount = interest / nMonths
    val avgMonthlyPayment: Amount = avgMonthlyPrincipalReduction + avgMonthlyInterest
}

/**
 * A summary of what happened with respect to the mortgage during a month.
 */
data class MortgageMonthSummary(
    /**
     * The mortgage payment for the month.
     */
    val mortgagePayment: MortgagePayment,
    /**
     * The status of the construction account for the month, or null if there is no construction account.
     */
    val constructionAccount: ConstructionAccountSummary?,
) {
    /**
     * The date of the payment for the month.
     */
    val date: LocalDate get() = mortgagePayment.date

    /**
     * The total amount effectively paid to the bank (with deductions applied).
     */
    val effectiveTotal: Amount = mortgagePayment.total - (constructionAccount?.deductedInterest ?: Amount.ZERO)
}

data class ConstructionAccountSummary(
    /**
     * The balance of the construction account at the start of the period.
     */
    val balanceBefore: Amount,
    /**
     * The construction bills that were paid from the construction account during the period.
     */
    val paidBills: List<Payment>,
    /**
     * The interest generated by the construction account for the period.
     */
    val generatedInterest: Amount,
    /**
     * The past interest generated by the construction account and removed from it to reduce the payments to the bank.
     */
    val deductedInterest: Amount,
)
