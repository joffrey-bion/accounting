package org.hildan.accounting.taxes.values

import org.hildan.accounting.taxes.GeneralTaxCredit
import org.hildan.accounting.taxes.LaborTaxCredit
import org.hildan.accounting.taxes.TaxSystem
import org.hildan.accounting.taxes.WageTax
import org.hildan.accounting.taxes.brackets.buildBrackets

internal fun NLTaxParameters.toTaxSystem() = TaxSystem(
    wageTax = WageTax(
        brackets = buildBrackets {
            bracket(rate = b1_1 ?: error("b1_1 was not set"), upTo = a2_1 ?: error("a2_1 was not set"))
            bracket(rate = b2_1 ?: error("b2_1 was not set"), upTo = a3_1 ?: error("a3_1 was not set"))
            lastBracket(rate = b3_1 ?: error("b3_1 was not set"))
        },
        specialRewardsTaxRate = specialRewardsTaxRate ?: error("specialTaxRate was not set"),
    ),
    generalTaxCredit = GeneralTaxCredit(
        maxCredit = ahkm1_1 ?: error("specialTaxRate was not set"),
        phaseOutThreshold = ahkg1 ?: error("specialTaxRate was not set"),
        phaseOutPercentage = ahka1_1 ?: error("specialTaxRate was not set"),
    ),
    laborTaxCredit = LaborTaxCredit(
        brackets = buildBrackets {
            bracket(rate = arko1_1 ?: error("arko1_1 was not set"), upTo = arkg1 ?: error("arkg1 was not set"))
            bracket(rate = arko2_1 ?: error("arko2_1 was not set"), upTo = arkg2 ?: error("arkg2 was not set"))
            bracket(rate = arko3_1 ?: error("arko3_1 was not set"), upTo = arkg3 ?: error("arkg3 was not set"))
            bracket(rate = arka4_1 ?: error("arka4_1 was not set"), upTo = arkg4 ?: error("arkg4 was not set"))
        }
    )
)
