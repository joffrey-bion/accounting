package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.taxes.brackets.AppliedBracket
import org.hildan.accounting.taxes.brackets.BracketSet

/**
 * The Labor Tax Credit (in Dutch, Arbeidskorting, or ARK) is a credit received by everyone who works in the
 * Netherlands (deducted from taxes by the employer when paying the salary).
 */
class LaborTaxCredit(
    private val brackets: BracketSet,
) {
    fun computeFor(grossAnnualTaxableIncome: Amount): TaxItem {
        val subItems = applyBrackets(grossAnnualTaxableIncome)
        return TaxItem(
            name = "Labor tax credit - Arbeidskorting (ARK)",
            description = "A tax credit received by everyone who works in the Netherlands (deducted from taxes by the employer when paying the salary).",
            totalAmount = subItems.sumOf { it.amount }.round(), // rounded to whole euros as per the PDF
            details = null,
            breakdown = subItems,
            type = TaxItemType.TAX_CREDIT,
        )
    }

    private fun applyBrackets(grossAnnualTaxableIncome: Amount) =
        brackets.applyTo(grossAnnualTaxableIncome).mapIndexed { i, appliedBracket ->
            TaxSubItem(
                name = "Labor tax credit (bracket ${i + 1})",
                description = null,
                amount = appliedBracket.result.roundToScale(scale = 5), // rounded to 5 decimals as per the PDF
                details = appliedWageTaxBracketDescription(appliedBracket, bracketNum = i + 1),
            )
        }
}

private fun appliedWageTaxBracketDescription(appliedBracket: AppliedBracket, bracketNum: Int) =
    "Labor tax credit equal to ${appliedBracket.bracket.rate} of ${appliedBracket.amountInBracket.format(scale = 0)}€, " +
        "which is the part of the gross annual taxable income that falls into bracket $bracketNum (${appliedBracket.bracket.range})."
