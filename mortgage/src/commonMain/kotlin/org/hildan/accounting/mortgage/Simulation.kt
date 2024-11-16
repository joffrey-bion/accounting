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
                val constructionAccountInterest = bdInterest(
                    bdStartBalance = constructionAccountBalance,
                    bills = paidBills,
                    dayCountConvention = mortgage.dayCountConvention,
                    period = payment.period,
                    interestRate = payment.averageInterestRateApplied,
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

                // if we're not deducting interest from the payments yet, we cumulate them on the construction account
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

private fun bdInterest(
    bdStartBalance: Amount,
    bills: List<Payment>,
    dayCountConvention: DayCountConvention,
    period: PaymentPeriod,
    interestRate: Fraction,
): Amount {
    var from = period.start
    var balance = bdStartBalance
    var interest = Amount.ZERO
    bills.forEach { bill ->
        val fractionOfMonth = dayCountConvention.dayCountFactor(start = from, endExclusive = bill.date)
        interest += balance * interestRate * fractionOfMonth
        balance -= bill.amount
        from = bill.date
    }
    val fractionOfMonth = dayCountConvention.dayCountFactor(start = from, endExclusive = period.endExclusive)
    interest += balance * interestRate * fractionOfMonth
    return interest
}
