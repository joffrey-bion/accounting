package org.hildan.accounting.taxes

import org.hildan.accounting.money.*

/**
 * The General Tax Credit (in Dutch, Algemene Heffingskorting, or AHK) is a credit received by everyone who lives in
 * the Netherlands for the whole year (deducted from taxes by the employer when paying the salary).
 */
class GeneralTaxCredit(
    private val maxCredit: Amount,
    private val phaseOutThreshold: Amount,
    private val phaseOutPercentage: Fraction,
) {
    fun computeFor(grossAnnualTaxableIncome: Amount): TaxItem {
        val excess = (grossAnnualTaxableIncome - phaseOutThreshold).coerceAtLeast(Amount.ZERO)
        val phaseOutReduction = excess * phaseOutPercentage
        val creditAmount = (maxCredit - phaseOutReduction).coerceAtLeast(Amount.ZERO) // non-negative as per the PDF
        return TaxItem(
            name = "General tax credit",
            description = "A tax credit received by everyone who lives in the Netherlands for the whole year " +
                "(deducted from taxes by the employer when paying the salary).",
            totalAmount = creditAmount.round(), // rounded to whole euros as per the PDF
            details = detailsMessage(phaseOutReduction, excess),
            breakdown = null,
            type = TaxItemType.TAX_CREDIT,
        )
    }

    private fun detailsMessage(phaseOutReduction: Amount, excess: Amount): String {
        if (excess == Amount.ZERO) {
            return "This is the maximum credit amount, because the gross salary does not exceed the phase-out " +
                "threshold of ${phaseOutThreshold.fEur()}."
        }
        val commentOnNegativeResult = if (phaseOutReduction > maxCredit) {
            " In this case, the phase out reduction is bigger than the maximum credit, so the credit is 0€ (it cannot" +
                " be negative)."
        } else {
            ""
        }
        return "This is obtained by starting from the maximum credit of ${maxCredit.fEur()}, and subtracting " +
            "${phaseOutReduction.fEur()} ($phaseOutPercentage of the ${excess.fEur()} that exceed the phase out " +
            "threshold of ${phaseOutThreshold.fEur()}).$commentOnNegativeResult"
    }
}

private fun Amount.fEur() = format(scale = 0) + "€"
