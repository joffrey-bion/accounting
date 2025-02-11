package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.interest.*

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
 * Runs a simulation based on these settings.
 */
fun SimulationSettings.simulate(): SimulationResult {
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
            val constructionAccountInterestDeadline = mortgage.startDate + property.constructionAccountInterestDuration

            val effectivePayments = mutableListOf<MortgageMonthSummary>()
            var deductPastInterest = false
            var interestToDeduct = Amount.ZERO
            mortgagePayments.forEach { payment ->
                val constructionAccountBalanceBefore = constructionAccountBalance
                val paidBills = sortedBills.paidIn(payment.period)

                val constructionAccountInterestPeriod = payment.period.withTruncatedEnd(constructionAccountInterestDeadline)

                // rounded to the cent because that's how the bank does it (it shows with whole cents in statements)
                val constructionAccountInterest = payment.averageInterestRateApplied.interestByPartsOn(
                    initialBalance = constructionAccountBalance,
                    balanceReductions = paidBills,
                    period = constructionAccountInterestPeriod,
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

private fun PaymentPeriod.withTruncatedEnd(maxEnd: LocalDate): PaymentPeriod =
    copy(endExclusive = endExclusive.coerceIn(start, maxEnd.coerceAtLeast(start)))
