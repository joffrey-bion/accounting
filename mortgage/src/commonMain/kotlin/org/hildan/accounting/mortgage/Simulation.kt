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
