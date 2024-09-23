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
    val firstMonthIsPartial = mortgage.startDate.dayOfMonth > 1
    val linearMonthlyPrincipalReduction = mortgage.amount / (mortgage.termInYears * 12)

    val sortedBills = SortedPayments(property.installments)
    val sortedExtraPayments = SortedPayments(mortgage.extraPayments)

    val totalPrice = property.installments.sumOf { it.amount }
    // we subtract the value because it will be added gradually through bills
    var mortgageBalance = mortgage.amount - totalPrice
    var interestPeriodStart = mortgage.startDate

    val payments = mutableListOf<MortgagePayment>()
    mortgage.monthlyPaymentDates.forEachIndexed { paymentIndex, paymentDate ->
        // TODO Track the construction account separately from the mortgage balance
        //  (currently the LTV ratio is incorrect, and the interest should be deducted on the next month only)
        mortgageBalance += sortedBills.removeAmountUpTo(paymentDate)

        val currentLtvRatio = mortgageBalance / property.wozValue
        val effectiveAnnualRate = mortgage.annualInterestRate.at(interestPeriodStart, currentLtvRatio = currentLtvRatio)
        
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
        val extraRedemption = sortedExtraPayments.removeAmountUpTo(paymentDate)

        val payment = MortgagePayment(
            date = paymentDate,
            principalReduction = principalReduction,
            extraPrincipalReduction = extraRedemption,
            interest = effectiveInterest,
            balanceBefore = mortgageBalance,
        )
        payments.add(payment)

        mortgageBalance -= extraRedemption
        mortgageBalance -= principalReduction

        // Interestingly, interest is not calculated between payment dates, but for complete months.
        // A payment on December 28th includes interest up to December 31st.
        // The next payment on January 30th (more than a month later) includes exactly one month of interest too.
        interestPeriodStart = paymentDate.nextMonthFirstDay()
    }
    return SimulationResult(settings = this, monthlyPayments = payments)
}

private fun LocalDate.nDaysInMonth(): Int = nextMonthFirstDay().minus(1, DateTimeUnit.DAY).dayOfMonth

private fun LocalDate.nextMonthFirstDay(): LocalDate {
    val sameDayNextMonth = plus(1, DateTimeUnit.MONTH)
    return LocalDate(year = sameDayNextMonth.year, month = sameDayNextMonth.month, dayOfMonth = 1)
}

private class SortedPayments(payments: List<Payment>) {
    private var sortedPayments = payments.sortedBy { it.date }
    
    fun removeAmountUpTo(dateInclusive: LocalDate): Amount {
        val found = sortedPayments.takeWhile { it.date <= dateInclusive }
        if (found.isNotEmpty()) {
            sortedPayments = sortedPayments.subList(found.size, sortedPayments.size)
        }
        return found.sumOf { it.amount }
    }
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
