package org.hildan.accounting.taxes

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.taxes.deductions.AppliedTaxDeduction
import org.hildan.accounting.taxes.values.NLTaxValues
import org.hildan.accounting.taxes.values.toTaxSystem

class TaxSystem(
    val wageTax: WageTax,
    val generalTaxCredit: GeneralTaxCredit,
    val laborTaxCredit: LaborTaxCredit,
) {
    fun computeTaxes(profile: Profile): List<TaxItem> {
        val salaryTaxItem = wageTax.onSalary(profile.grossAnnualTaxableSalary)
        val bonusTaxItem = wageTax.onBonus(profile.grossAnnualTaxableBonus)

        val generalTaxCreditItem = generalTaxCredit.computeFor(profile.grossAnnualTaxableSalary)
        val laborTaxCreditItem = laborTaxCredit.computeFor(profile.grossAnnualTaxableSalary)
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
            add(taxDeductionsOnSalary(profile))
            add(TaxItem(
                name = "Taxable gross annual salary",
                description = "The part of the gross salary to which taxes are applied (after tax deductions)",
                totalAmount = profile.grossAnnualTaxableSalary,
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
            add(
                TaxItem(
                    name = "Tax deductions on bonus",
                    description = "Amounts of money subtracted from the gross bonus to get the taxable bonus",
                    totalAmount = profile.appliedTaxDeductionsOnBonus.sumOf { it.effectiveDiscount },
                    details = null,
                    breakdown = profile.appliedTaxDeductionsOnBonus.map { it.toTaxSubItem() },
                    type = TaxItemType.TAX_DEDUCTION,
                )
            )
            add(
                TaxItem(
                    name = "Taxable gross bonus",
                    description = "The part of the gross bonus to which taxes are applied (after tax deductions)",
                    totalAmount = profile.grossAnnualTaxableBonus,
                    type = TaxItemType.INCOME,
                )
            )
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

    private fun taxDeductionsOnSalary(profile: Profile): TaxItem = TaxItem(
        name = "Tax deductions on salary",
        description = "Amounts of money subtracted from the gross salary to get the taxable salary",
        totalAmount = profile.appliedTaxDeductionsOnSalary.sumOf { it.effectiveDiscount },
        details = null,
        breakdown = profile.appliedTaxDeductionsOnSalary.map { it.toTaxSubItem() },
        type = TaxItemType.TAX_DEDUCTION,
    )

    companion object {
        fun forYear(year: Int) = NLTaxValues.forYear(year).toTaxSystem()
    }
}

private fun AppliedTaxDeduction.toTaxSubItem() = TaxSubItem(
    name = deduction.name,
    description = deduction.explanation,
    amount = effectiveDiscount,
    details = null,
)
