package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.taxes.brackets.AppliedBracket
import org.hildan.accounting.taxes.brackets.BracketSet

/**
 * A tax on the annual income (Loonbelasting).
 */
class WageTax(
    private val brackets: BracketSet,
    private val specialRewardsTaxRate: Fraction,
) {
    fun onSalary(grossAnnualTaxableSalary: Amount): TaxItem {
        val subItems = applyBrackets(grossAnnualTaxableSalary)
        return TaxItem(
            name = "Wage tax",
            description = "The tax on the annual wages, calculated by applying different tax rates on different " +
                    "parts of the annual income (brackets). It is always calculated on the full annual salary, as if " +
                    "a whole year was worked. For partial years (start or stop working at the company mid-year)," +
                    "the difference is adjusted in the tax return.",
            totalAmount = subItems.sumOf { it.amount }.rounded(),
            details = null,
            breakdown = subItems,
            type = TaxItemType.TAX,
        )
    }

    private fun applyBrackets(grossAnnualTaxableIncome: Amount) =
        brackets.applyTo(grossAnnualTaxableIncome).mapIndexed { i, appliedBracket ->
            TaxSubItem(
                name = "Wage tax on bracket ${i + 1}",
                description = null,
                amount = appliedBracket.result,
                details = appliedWageTaxBracketDescription(appliedBracket),
            )
        }

    fun onBonus(grossTaxableBonus: Amount): TaxItem = TaxItem(
        name = "Wage tax on bonus",
        description = null,
        totalAmount = grossTaxableBonus * specialRewardsTaxRate,
        details = "This tax is applied with the $specialRewardsTaxRate special reward tax rate (Bijzondere beloningen," +
            " or Bijzonder Tarief - BT)",
        breakdown = null,
        type = TaxItemType.TAX,
    )
}

private fun appliedWageTaxBracketDescription(appliedBracket: AppliedBracket) =
    "Wage tax equal to ${appliedBracket.bracket.rate} of ${appliedBracket.amountInBracket.format(scale = 0)}€, which " +
        "is the part of the gross annual taxable salary that falls into the bracket range ${appliedBracket.bracket.range}."