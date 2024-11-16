package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*

/**
 * Defines the conditions of a mortgage.
 */
data class Mortgage(
    /**
     * The total amount borrowed.
     */
    val amount: Amount,
    /**
     * The annual interest rate. This fraction of the current balance of the mortgage (the amount that has not been
     * repaid yet) will have to be paid every year. It is usually paid monthly (1/12th of the rate).
     */
    val annualInterestRate: InterestRate,
    /**
     * The start date of the loan (when signed at the notary's office).
     * This is the moment when the funds are released and interest starts being due.
     */
    val startDate: LocalDate,
    /**
     * The payments made voluntarily to pay back the loan, usually to reduce the interest and thus the monthly payments.
     */
    val extraPayments: List<Payment> = emptyList(),
    /**
     * The total duration (in years) over which the mortgage will be repaid.
     */
    val termInYears: Int = 30,
    /**
     * The day-count rule for this mortgage, which defines how interest is applied to partial months.
     */
    val dayCountConvention: DayCountConvention = DayCountConvention.ActualActual,
) {
    /**
     * The dates of the monthly payments for the duration of the mortgage.
     */
    val monthlyPaymentDates: List<LocalDate> = monthlyPaymentDates(startDate, termInYears, dayOfMonth = 28)
}

/**
 * Returns the monthly payment dates based on the [startDate] of the loan and the [termInYears], assuming a fixed day
 * [dayOfMonth] each month.
 */
internal fun monthlyPaymentDates(startDate: LocalDate, termInYears: Int, dayOfMonth: Int): List<LocalDate> {
    val firstPayment = LocalDate(startDate.year, startDate.month, dayOfMonth)
    val firstMonthIsPartial = startDate.dayOfMonth > 1
    val redemptionDay = startDate.plus(termInYears, DateTimeUnit.YEAR).let {
        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        if (firstMonthIsPartial) it.plus(1, DateTimeUnit.MONTH) else it
    }
    return generateSequence(firstPayment) { it.plus(1, DateTimeUnit.MONTH) }
        .takeWhile { it <= redemptionDay }
        .toList()
}

/**
 * Calculates the monthly payments for this [Mortgage] assuming a linear reimbursement scheme.
 */
internal fun Mortgage.calculatePaymentsLinear(propertyWozValue: (LocalDate) -> Amount): List<MortgagePayment> {
    val firstMonthIsPartial = startDate.dayOfMonth > 1

    val remainingExtraPayments = SortedPayments(extraPayments)

    var mortgageBalance = amount
    var interestPeriodStart = startDate

    val payments = mutableListOf<MortgagePayment>()
    monthlyPaymentDates.forEachIndexed { paymentIndex, paymentDate ->
        val remainingMonths = monthlyPaymentDates.size - paymentIndex

        val currentLtvRatio = mortgageBalance / propertyWozValue(paymentDate)
        val effectiveAnnualRate = annualInterestRate.at(interestPeriodStart, currentLtvRatio = currentLtvRatio)

        val nextPeriodStart = paymentDate.nextMonthFirstDay()
        val dayCountFactor = dayCountConvention.dayCountFactor(start = interestPeriodStart, endExclusive = nextPeriodStart)
        val effectiveInterest = mortgageBalance.coerceAtLeast(Amount.ZERO) * effectiveAnnualRate * dayCountFactor

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
