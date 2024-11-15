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
                val paidBills = sortedBills.popPaymentsUntil(payment.nextPeriodStart)

                // rounded to the cent because that's how the bank does it (it shows with whole cents in statements)
                val constructionAccountInterest = bdInterest(
                    payment = payment,
                    bdStartBalance = constructionAccountBalance,
                    bills = paidBills,
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

                // FIXME we don't handle the transition well here, double check when we credit/remove interest
                // if we're not deducting interest from the payments yet, we cumulate them on the construction account
                if (deductPastInterest) {
                    interestToDeduct = Amount.ZERO // consume it
                } else {
                    constructionAccountBalance += constructionAccountInterest
                }
                interestToDeduct += constructionAccountInterest

                // we start deducting the construction fund interest after the first real payment occurs (first full month)
                val isPartialMonth = payment.periodStart.dayOfMonth > 1
                if (!isPartialMonth) {
                    deductPastInterest = true
                }
            }

            SimulationResult(settings = this, monthSummaries = effectivePayments)
        }
    }
}

private fun bdInterest(
    payment: MortgagePayment,
    bdStartBalance: Amount,
    bills: List<Payment>,
    dayCountConvention: DayCountConvention,
): Amount {
    val monthlyRate = payment.appliedInterestRate / 12
    var from = payment.periodStart
    var balance = bdStartBalance
    var interest = Amount.ZERO
    bills.forEach { bill ->
        val fractionOfMonth = dayCountConvention.monthRatio(start = from, endExclusive = bill.date)
        interest += balance * monthlyRate * fractionOfMonth
        balance -= bill.amount
        from = bill.date
    }
    val fractionOfMonth = dayCountConvention.monthRatio(start = from, endExclusive = payment.nextPeriodStart)
    interest += balance * monthlyRate * fractionOfMonth
    return interest
}

/**
 * Calculates the monthly payments for this [Mortgage] assuming a linear reimbursement scheme.
 */
private fun Mortgage.calculatePaymentsLinear(propertyWozValue: (LocalDate) -> Amount): List<MortgagePayment> {
    val firstMonthIsPartial = startDate.dayOfMonth > 1

    val remainingExtraPayments = SortedPayments(extraPayments)

    var mortgageBalance = amount
    var interestPeriodStart = startDate

    val payments = mutableListOf<MortgagePayment>()
    monthlyPaymentDates.forEachIndexed { paymentIndex, paymentDate ->
        val remainingMonths = monthlyPaymentDates.size - paymentIndex

        val currentLtvRatio = mortgageBalance / propertyWozValue(paymentDate)
        val effectiveAnnualRate = annualInterestRate.at(interestPeriodStart, currentLtvRatio = currentLtvRatio)

        val fullMonthInterest = mortgageBalance.coerceAtLeast(Amount.ZERO) * effectiveAnnualRate / 12
        val nextPeriodStart = paymentDate.nextMonthFirstDay()
        val effectiveInterest = if (paymentIndex == 0 && firstMonthIsPartial) {
            val monthFraction = dayCountConvention.monthRatio(start = interestPeriodStart, endExclusive = nextPeriodStart)
            fullMonthInterest * monthFraction
        } else {
            fullMonthInterest
        }

        // Not sure how the bank gets a round number in the total, so we round both principal and interest to get this
        val linearMonthlyPrincipalReduction = (mortgageBalance / remainingMonths).roundedToTheCent()
        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        val principalReduction = if (paymentIndex == 0 && firstMonthIsPartial) Amount.ZERO else linearMonthlyPrincipalReduction

        // We count all the extra payments of the month, even the ones that are technically after the mandatory payment
        // date, because our goal is to aggregate per month
        val extraPaymentsThisMonth = remainingExtraPayments.popPaymentsUntil(nextPeriodStart)
        val extraPrincipalReduction = extraPaymentsThisMonth.sumOf { it.amount }

        val payment = MortgagePayment(
            date = paymentDate,
            periodStart = interestPeriodStart,
            nextPeriodStart = nextPeriodStart,
            balanceBefore = mortgageBalance,
            principalReduction = principalReduction,
            extraPrincipalReduction = extraPrincipalReduction,
            appliedInterestRate = effectiveAnnualRate,
            // Not sure how the bank gets a round number in the total, so we round both principal and interest to get this
            interest = effectiveInterest.roundedToTheCent(),
        )
        payments.add(payment)

        mortgageBalance -= extraPrincipalReduction
        mortgageBalance -= principalReduction

        // Interestingly, interest is not calculated between payment dates, but for complete months.
        // A payment on December 28th includes interest up to December 31st.
        // The next payment on January 30th (more than a month later) includes exactly one month of interest too.
        interestPeriodStart = nextPeriodStart
    }
    return payments
}

private class SortedPayments(payments: List<Payment>) {
    private var sortedPayments = payments.sortedBy { it.date }

    fun popPaymentsUntil(dateExclusive: LocalDate): List<Payment> {
        val bills = sortedPayments.takeWhile { it.date < dateExclusive }
        if (bills.isNotEmpty()) {
            sortedPayments = sortedPayments.drop(bills.size)
        }
        return bills
    }
}
