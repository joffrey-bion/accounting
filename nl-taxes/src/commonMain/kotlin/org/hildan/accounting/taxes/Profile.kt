package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.pct
import org.hildan.accounting.money.sumOf

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
     * Whether the person is eligible to the 30% rule.
     */
    val rule30p: Boolean,
    /**
     * The tax deductions that the user is eligible to.
     */
    val taxDeductions: List<TaxSubItem> = emptyList(),
) {
    val grossAnnualTaxableSalary = grossAnnualSalary.reduce30p() - taxDeductions.sumOf { it.amount }
    val grossAnnualTaxableBonus = grossAnnualBonus.reduce30p()

    private fun Amount.reduce30p() = if (rule30p) this * 70.pct else this
}
