package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*
import kotlin.jvm.JvmInline

/**
 * Identifies one part of a mortgage.
 */
@JvmInline
value class MortgagePartId(val id: String)

/**
 * Defines the conditions of a mortgage.
 */
data class Mortgage(
    /**
     * The start date of the loan (when signed at the notary's office).
     * This is the moment when the funds are released and interest starts being due.
     */
    val startDate: LocalDate,
    /**
     * The total duration (in years) over which the mortgage will be repaid.
     */
    val termInYears: Int = 30,
    /**
     * Defines the conditions of each mortgage part.
     */
    val parts: List<MortgagePart>,
    /**
     * The day-count convention for this mortgage, which defines how interest is applied to partial months.
     */
    val dayCountConvention: DayCountConvention = DayCountConvention.ActualActual,
) {
    /**
     * The total amount borrowed.
     */
    val amount: Amount = parts.sumOf { it.amount }
}

/**
 * Defines the conditions of a mortgage part.
 */
data class MortgagePart(
    /**
     * A name to identify this part of the mortgage.
     */
    val id: MortgagePartId,
    /**
     * The total amount borrowed for this part.
     */
    val amount: Amount,
    /**
     * The annual interest rate. This fraction of the current balance of the mortgage (the amount that has not been
     * repaid yet) will have to be paid every year. It is usually paid monthly (1/12th of the rate).
     */
    val annualInterestRate: InterestRate,
    /**
     * The payments made voluntarily to pay back the loan, usually to reduce the interest and thus the monthly payments.
     */
    val extraPayments: List<Payment> = emptyList(),
)

/**
 * Calculates the monthly payments for this [Mortgage] assuming a linear reimbursement scheme.
 */
internal fun Mortgage.simulatePayments(propertyWozValue: (LocalDate) -> Amount): List<MortgagePayment> {
    val monthlyPaymentPeriods = monthlyPaymentPeriods(startDate, termInYears)
    val partSimulators = parts.map { PartSimulator(part = it, dayCountConvention, monthlyPaymentPeriods) }
    return buildList {
        while (true) {
            val mortgageBalance = partSimulators.sumOf { part -> part.balance }
            val payments = partSimulators.mapNotNull { partSim ->
                partSim.simulateNextMonth(currentLtvRatio = { period -> mortgageBalance / propertyWozValue(period.start) })
            }
            if (payments.isEmpty()) {
                break
            }
            add(MortgagePayment(payments))
        }
    }
}

/**
 * Returns the monthly payment periods based on the [startDate] of the loan and the [termInYears].
 */
private fun monthlyPaymentPeriods(startDate: LocalDate, termInYears: Int): List<PaymentPeriod> {
    val firstMonthIsPartial = startDate.dayOfMonth > 1
    val redemptionDay = startDate.plus(termInYears, DateTimeUnit.YEAR).let {
        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        if (firstMonthIsPartial) it.plus(1, DateTimeUnit.MONTH) else it
    }
    return generateSequence(startDate) { it.nextMonthFirstDay() }
        .takeWhile { it <= redemptionDay }
        .zipWithNext { d1, d2 -> PaymentPeriod(d1, d2) }
        .toList()
}

private class PartSimulator(
    val part: MortgagePart,
    val dayCountConvention: DayCountConvention,
    val monthlyPaymentPeriods: List<PaymentPeriod>,
) {
    private val sortedExtraPayments = SortedPayments(part.extraPayments)

    var balance = part.amount
        private set

    private var nextMonthIndex = 0

    fun simulateNextMonth(currentLtvRatio: (PaymentPeriod) -> Fraction): MortgagePartPayment? {
        if (nextMonthIndex !in monthlyPaymentPeriods.indices) {
            return null
        }
        val monthIndex = nextMonthIndex++
        val period = monthlyPaymentPeriods[monthIndex]
        return simulateMonth(
            period = period,
            currentLtvRatio = currentLtvRatio(period),
            remainingMonths = monthlyPaymentPeriods.size - monthIndex
        )
    }

    private fun simulateMonth(period: PaymentPeriod, currentLtvRatio: Fraction, remainingMonths: Int): MortgagePartPayment {
        val annualRate = part.annualInterestRate.at(period.start, currentLtvRatio = currentLtvRatio)

        val linearMonthlyPrincipalReduction = balance / remainingMonths
        // We only start paying back the principal on the first full month.
        // If the first month is partial, we just pay interest.
        val principalReduction = if (period.start.dayOfMonth > 1) Amount.ZERO else linearMonthlyPrincipalReduction

        val extraRepayments = sortedExtraPayments.paidIn(period)
        val interest = interestByParts(
            initialBalance = balance,
            balanceReductions = extraRepayments,
            period = period,
            annualInterestRate = annualRate,
            dayCountConvention = dayCountConvention,
        )

        // When extra payments are made, the bank keeps the total payment due the same, and compensates for the extra
        // interest perceived by counting it as more principal reduction
        val interestIgnoringExtraPayments = balance * annualRate * dayCountConvention.dayCountFactor(period)
        val totalIgnoringExtraPayments = (principalReduction + interestIgnoringExtraPayments).roundedToTheCent()
        val effectivePrincipalReduction = totalIgnoringExtraPayments - interest

        val payment = MortgagePartPayment(
            partId = part.id,
            date = period.start.withDayOfMonth(28), // TODO use real last working day
            period = period,
            balanceBefore = balance,
            principalReduction = effectivePrincipalReduction,
            extraPrincipalReduction = extraRepayments.sumOf { it.amount },
            appliedInterestRate = annualRate,
            interest = interest,
        )

        balance -= payment.extraPrincipalReduction
        balance -= payment.principalReduction

        // the bank rounds the mortgage balance (and the total payment), but not the principal reduction individually
        balance = balance.roundedToTheCent()
        return payment
    }
}
