package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.pct
import org.hildan.accounting.money.sumOf

data class TaxProfile(
    /**
     * The gross annual salary, including 8% holiday allowance.
     */
    val grossAnnualSalary: Amount,
    /**
     * Whether the person is eligible to the 30% rule.
     */
    val rule30p: Boolean,
    /**
     * The tax deductions that the user is eligible to.
     */
    val taxDeductions: List<TaxSubItem> = emptyList(),
) {
    // TODO rate adjustment: deductions cannot have a rate higher than the first tax bracket rate
    val grossAnnualTaxableSalary = grossAnnualSalary.reduce30p() - taxDeductions.sumOf { it.amount }

    private fun Amount.reduce30p() = if (rule30p) this * 70.pct else this
}
