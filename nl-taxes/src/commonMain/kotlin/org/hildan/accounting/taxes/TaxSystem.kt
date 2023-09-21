package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.pct
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.taxes.values.NLTaxValues
import org.hildan.accounting.taxes.values.toTaxSystem

class TaxSystem(
    val wageTax: WageTax,
    val generalTaxCredit: GeneralTaxCredit,
    val laborTaxCredit: LaborTaxCredit,
) {
    fun computeTaxes(profile: Profile): List<TaxItem> {
        val grossAnnualTaxableSalary = profile.grossAnnualTaxableSalary
        val salaryTaxItem = wageTax.onSalary(grossAnnualTaxableSalary)
        val bonusTaxItem = wageTax.onBonus(profile.grossAnnualTaxableBonus)

        val generalTaxCreditItem = generalTaxCredit.computeFor(grossAnnualTaxableSalary)
        val laborTaxCreditItem = laborTaxCredit.computeFor(grossAnnualTaxableSalary)
        val totalTaxCreditsItem = TaxItem(
            name = "Total tax credits",
            totalAmount = generalTaxCreditItem.totalAmount + laborTaxCreditItem.totalAmount,
            type = TaxItemType.TAX_CREDIT,
        )

        return buildList {
            add(TaxItem(
                name = "Gross annual salary",
                description = "The annual salary received from the employer, including the 8% holiday allowance",
                totalAmount = profile.grossAnnualSalary,
                type = TaxItemType.INCOME,
            ))
            if (profile.rule30p) {
                add(TaxItem(
                    name = "30% ruling deduction on salary",
                    description = "Makes the taxable salary 30% smaller due to the 30% rule eligibility",
                    totalAmount = profile.grossAnnualSalary * 30.pct,
                    details = "30% of the gross salary of ${profile.grossAnnualSalary.format(scale = 0)}",
                    breakdown = null,
                    type = TaxItemType.TAX_DEDUCTION,
                ))
            }
            if (profile.taxDeductions.isNotEmpty()) {
                add(TaxItem(
                    name = "Tax deductions on salary",
                    description = "Amounts of money subtracted from the gross salary before applying taxes",
                    totalAmount = profile.taxDeductions.sumOf { it.amount },
                    details = null,
                    breakdown = profile.taxDeductions,
                    type = TaxItemType.TAX_DEDUCTION,
                ))
            }
            add(TaxItem(
                name = "Taxable gross annual salary",
                description = "The part of the gross salary to which taxes are applied (after tax deductions)",
                totalAmount = grossAnnualTaxableSalary,
                details = "No tax deductions nor 30% ruling, hence why it's the same amount as the gross annual salary"
                    .takeIf { !profile.rule30p && profile.taxDeductions.isEmpty() },
                type = TaxItemType.INCOME,
            ))
            add(salaryTaxItem)

            val bonusItems = getBonusItems(profile, bonusTaxItem)
            if (bonusItems != null) {
                addAll(bonusItems)
                add(
                    TaxItem(
                        name = "Total income tax",
                        totalAmount = salaryTaxItem.totalAmount + bonusTaxItem.totalAmount,
                        type = TaxItemType.TAX,
                    )
                )
            }
            add(generalTaxCreditItem)
            add(laborTaxCreditItem)
            add(totalTaxCreditsItem)
            add(TaxItem(
                name = "Net annual salary",
                description = "The salary after tax and tax credits",
                totalAmount = profile.grossAnnualSalary - salaryTaxItem.totalAmount + totalTaxCreditsItem.totalAmount,
                type = TaxItemType.INCOME,
            ))
        }
    }

    private fun getBonusItems(profile: Profile, bonusTaxItem: TaxItem): List<TaxItem>? {
        if (profile.grossAnnualBonus == Amount.ZERO) {
            return null
        }
        return buildList {
            add(
                TaxItem(
                    name = "Gross annual bonus",
                    description = "An annual bonus received from the employer",
                    totalAmount = profile.grossAnnualBonus,
                    type = TaxItemType.INCOME,
                )
            )
            if (profile.rule30p) {
                add(
                    TaxItem(
                        name = "30% ruling deduction on bonus",
                        description = "Makes the taxable bonus 30% smaller due to the 30% rule eligibility",
                        totalAmount = profile.grossAnnualBonus * 30.pct,
                        details = "30% of the gross bonus of ${profile.grossAnnualBonus.format(scale = 0)}",
                        breakdown = null,
                        type = TaxItemType.TAX_DEDUCTION,
                    )
                )
                add(
                    TaxItem(
                        name = "Taxable gross bonus",
                        description = "The part of the gross bonus to which taxes are applied (after tax deductions)",
                        totalAmount = profile.grossAnnualBonus * 70.pct,
                        type = TaxItemType.INCOME,
                    )
                )
            }
            add(bonusTaxItem)
            add(
                TaxItem(
                    name = "Net bonus",
                    description = "The bonus after tax",
                    totalAmount = profile.grossAnnualBonus - bonusTaxItem.totalAmount,
                    type = TaxItemType.INCOME,
                )
            )
        }
    }

    companion object {
        fun forYear(year: Int) = NLTaxValues.forYear(year).toTaxSystem()
    }
}
