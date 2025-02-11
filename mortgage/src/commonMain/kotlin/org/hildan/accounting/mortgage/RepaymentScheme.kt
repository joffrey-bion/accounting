package org.hildan.accounting.mortgage

import org.hildan.accounting.money.Amount
import org.hildan.accounting.mortgage.interest.*

/**
 * The principal repayment scheme defines how much of the principal is repaid throughout the months.
 *
 * The interest cannot be controlled because it only depends on the interest rate and the mortgage total principal, but
 * we can decide to repay the principal in different ways.
 */
enum class RepaymentScheme {
    /**
     * The principal is reimbursed in constant amounts (the total is divided equally among all months), so when we add
     * the interest, the total payments are linearly decreasing over time.
     */
    Linear {
        override fun principalRepayment(
            balance: Amount,
            interestRate: ApplicableInterestRate,
            remainingMonths: Int,
        ): Amount {
            return balance / remainingMonths
        }
    },

    /**
     * The total payments (principal + interest) are constant over time.
     * The principal repayment is therefore adjusted in just the right way to compensate for the interest decrease.
     */
    Annuity {
        override fun principalRepayment(
            balance: Amount,
            interestRate: ApplicableInterestRate,
            remainingMonths: Int,
        ): Amount {
            require(interestRate.dayCountConvention != DayCountConvention.ActualActual) {
                "Annuity repayment scheme is incompatible with Actual/Actual day count convention, " +
                    "please use a 30/360 convention"
            }
            // See https://github.com/joffrey-bion/accounting/issues/60 for how to get this formula
            val mRate = interestRate.annualRate / 12
            return balance * mRate / ((mRate + 1).pow(remainingMonths) - 1)
        }
    };

    abstract fun principalRepayment(
        balance: Amount,
        interestRate: ApplicableInterestRate,
        remainingMonths: Int,
    ): Amount
}
