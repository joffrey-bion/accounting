package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.taxes.deductions.TaxDeduction
import org.hildan.accounting.taxes.deductions.applyTo

data class Profile(
    /**
     * The gross annual salary, including 8% holiday allowance.
     */
    val grossAnnualSalary: Amount,
    /**
     * The gross annual bonus.
     */
    val grossAnnualBonus: Amount = Amount.ZERO,
    /**
     * The tax deductions that the user is eligible to.
     */
    val taxDeductions: List<TaxDeduction>,
) {
    val appliedTaxDeductionsOnSalary = taxDeductions.applyTo(grossAnnualSalary)
    val appliedTaxDeductionsOnBonus = taxDeductions.filterIsInstance<TaxDeduction.Multiplicative>().applyTo(grossAnnualBonus)
    val grossAnnualTaxableSalary = grossAnnualSalary - appliedTaxDeductionsOnSalary.sumOf { it.effectiveDiscount }
    val grossAnnualTaxableBonus = grossAnnualBonus - appliedTaxDeductionsOnBonus.sumOf { it.effectiveDiscount }
}
