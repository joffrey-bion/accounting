package org.hildan.accounting.mortgage

import kotlinx.datetime.*
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
    val mortgagePayments = mortgage.calculatePaymentsLinear(propertyWozValue = { property.wozValue })
    return when (property) {
        is Property.Existing -> SimulationResult(
            settings = this,
            monthSummaries = mortgagePayments.map {
                MortgageMonthSummary(
                    date = it.date,
                    mortgagePayment = it,
                    constructionAccount = null,
                )
            },
        )
        is Property.NewConstruction -> {
            val sortedBills = SortedPayments(property.constructionInstallments)
            var constructionAccountBalance = property.constructionInstallments.sumOf { it.amount }

            val effectivePayments = mutableListOf<MortgageMonthSummary>()
            var nextDeductedInterest = Amount.ZERO
            mortgagePayments.forEach { payment ->
                val constructionAccountBalanceBefore = constructionAccountBalance

                // FIXME interest is wrong for partial month - follow the example in calculatePaymentsLinear()
                val constructionAccountInterest = constructionAccountBalance * payment.appliedInterestRate / 12

                // TODO should we count the interest before/after each bill?
                constructionAccountBalance -= sortedBills.removeAmountUntil(payment.date)

                effectivePayments.add(MortgageMonthSummary(
                    date = payment.date,
                    mortgagePayment = payment,
                    constructionAccount = ConstructionAccountSummary(
                        balanceBefore = constructionAccountBalanceBefore,
                        generatedInterest = constructionAccountInterest,
                        deductedInterest = nextDeductedInterest,
                    ),
                ))
                // TODO the first partial month doesn't result in a payment, so the first 2 months should credit their
                //  interest to the construction account, not just the first.
                if (nextDeductedInterest == Amount.ZERO) {
                    constructionAccountBalance += constructionAccountInterest
                }
                nextDeductedInterest = constructionAccountInterest
            }

            SimulationResult(settings = this, monthSummaries = effectivePayments)
        }
    }
}

/**
 * Calculates the monthly payments for this [Mortgage] assuming a linear reimbursement scheme.
 */
private fun Mortgage.calculatePaymentsLinear(propertyWozValue: (LocalDate) -> Amount): List<MortgagePayment> {
    val firstMonthIsPartial = startDate.dayOfMonth > 1

    // FIXME should be adjusted when extra payments are made (based on the remaining months)
    val linearMonthlyPrincipalReduction = amount / (termInYears * 12)

    val remainingExtraPayments = SortedPayments(extraPayments)

    var mortgageBalance = amount
    var interestPeriodStart = startDate

    val payments = mutableListOf<MortgagePayment>()
    monthlyPaymentDates.forEachIndexed { paymentIndex, paymentDate ->
        val currentLtvRatio = mortgageBalance / propertyWozValue(paymentDate)
        val effectiveAnnualRate = annualInterestRate.at(interestPeriodStart, currentLtvRatio = currentLtvRatio)

        val fullMonthInterest = mortgageBalance.coerceAtLeast(Amount.ZERO) * effectiveAnnualRate / 12
        val effectiveInterest = if (paymentIndex == 0 && firstMonthIsPartial) {
            val interestPeriodDays = interestPeriodStart.daysUntil(paymentDate.nextMonthFirstDay())
            val totalDaysInMonth = paymentDate.nDaysInMonth()
            fullMonthInterest * interestPeriodDays / totalDaysInMonth
        } else {
            fullMonthInterest
        }

        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        val principalReduction = if (paymentIndex == 0 && firstMonthIsPartial) Amount.ZERO else linearMonthlyPrincipalReduction

        // We count all the extra payments of the month, even the ones that are technically after the mandatory payment
        // date, because our goal is to aggregate per month
        val extraRedemption = remainingExtraPayments.removeAmountUntil(paymentDate.nextMonthFirstDay())

        val payment = MortgagePayment(
            date = paymentDate,
            balanceBefore = mortgageBalance,
            principalReduction = principalReduction,
            extraPrincipalReduction = extraRedemption,
            appliedInterestRate = effectiveAnnualRate,
            interest = effectiveInterest,
        )
        payments.add(payment)

        mortgageBalance -= extraRedemption
        mortgageBalance -= principalReduction

        // Interestingly, interest is not calculated between payment dates, but for complete months.
        // A payment on December 28th includes interest up to December 31st.
        // The next payment on January 30th (more than a month later) includes exactly one month of interest too.
        interestPeriodStart = paymentDate.nextMonthFirstDay()
    }
    return payments
}

private fun LocalDate.nDaysInMonth(): Int = nextMonthFirstDay().minus(1, DateTimeUnit.DAY).dayOfMonth

private fun LocalDate.nextMonthFirstDay(): LocalDate = firstOfMonthOf(this).plus(1, DateTimeUnit.MONTH)

private fun firstOfMonthOf(date: LocalDate) = LocalDate(year = date.year, month = date.month, dayOfMonth = 1)

private class SortedPayments(payments: List<Payment>) {
    private var sortedPayments = payments.sortedBy { it.date }

    fun removeAmountUntil(dateExclusive: LocalDate): Amount {
        val found = sortedPayments.takeWhile { it.date < dateExclusive }
        if (found.isNotEmpty()) {
            sortedPayments = sortedPayments.drop(found.size)
        }
        return found.sumOf { it.amount }
    }
}
