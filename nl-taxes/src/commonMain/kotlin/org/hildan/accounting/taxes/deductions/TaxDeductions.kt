package org.hildan.accounting.taxes.deductions

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.money.pct

fun List<TaxDeduction>.applyTo(grossAmount: Amount): List<AppliedTaxDeduction> =
   map { AppliedTaxDeduction(effectiveDiscount = it.compute(grossAmount), deduction = it) }

data class AppliedTaxDeduction(
    val effectiveDiscount: Amount,
    val deduction: TaxDeduction,
)

sealed interface TaxDeduction {
    val name: String
    val explanation: String

    /**
     * Computes the amount that will be subtracted from the gross amount.
     */
    fun compute(grossAmount: Amount): Amount

    data class Subtractive(
        override val name: String,
        override val explanation: String,
        /**
         * The amount subtracted from the gross income.
         */
        val reduction: Amount,
    ) : TaxDeduction {
        override fun compute(grossAmount: Amount) = reduction
    }

    data class Multiplicative(
        override val name: String,
        override val explanation: String,
        /**
         * The fraction of the income that is not taxable.
         */
        val taxFreeFraction: Fraction,
    ) : TaxDeduction {
        override fun compute(grossAmount: Amount) = grossAmount * taxFreeFraction
    }

    companion object {
        val Rule30p = Multiplicative(
            name = "30% Rule",
            explanation = "Expats with a certain level of salary or education have 30% of their salary untaxed.",
            taxFreeFraction = 30.pct,
        )
    }
}
