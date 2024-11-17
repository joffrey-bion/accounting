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
    val mortgagePayments = mortgage.simulatePayments(propertyWozValue = { property.wozValue })
    return when (property) {
        is Property.Existing -> SimulationResult(
            settings = this,
            monthSummaries = mortgagePayments.map {
                MortgageMonthSummary(
                    mortgagePayment = it,
                    constructionAccount = null,
                )
            },
        )
        is Property.NewConstruction -> {
            val sortedBills = SortedPayments(property.constructionInstallments)
            var constructionAccountBalance = property.constructionInstallments.sumOf { it.amount }

            val effectivePayments = mutableListOf<MortgageMonthSummary>()
            var deductPastInterest = false
            var interestToDeduct = Amount.ZERO
            mortgagePayments.forEach { payment ->
                val constructionAccountBalanceBefore = constructionAccountBalance
                val paidBills = sortedBills.paidIn(payment.period)

                // rounded to the cent because that's how the bank does it (it shows with whole cents in statements)
                val constructionAccountInterest = interestByParts(
                    initialBalance = constructionAccountBalance,
                    balanceReductions = paidBills,
                    period = payment.period,
                    annualInterestRate = payment.averageInterestRateApplied,
                    dayCountConvention = mortgage.dayCountConvention,
                ).roundedToTheCent()

                constructionAccountBalance -= paidBills.sumOf { it.amount }

                effectivePayments.add(MortgageMonthSummary(
                    mortgagePayment = payment,
                    constructionAccount = ConstructionAccountSummary(
                        balanceBefore = constructionAccountBalanceBefore,
                        paidBills = paidBills,
                        generatedInterest = constructionAccountInterest,
                        deductedInterest = if (deductPastInterest) interestToDeduct else Amount.ZERO,
                    ),
                ))

                if (deductPastInterest) {
                    constructionAccountBalance -= interestToDeduct
                    interestToDeduct = Amount.ZERO // consume it
                }
                constructionAccountBalance += constructionAccountInterest
                interestToDeduct += constructionAccountInterest

                // we start deducting the construction fund interest after the first real payment occurs (first full month)
                val isPartialMonth = payment.period.start.dayOfMonth > 1
                if (!isPartialMonth) {
                    deductPastInterest = true
                }
            }

            SimulationResult(settings = this, monthSummaries = effectivePayments)
        }
    }
}
