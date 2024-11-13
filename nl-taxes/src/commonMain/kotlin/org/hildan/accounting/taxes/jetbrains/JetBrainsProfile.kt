package org.hildan.accounting.taxes.jetbrains

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.taxes.TaxProfile
import org.hildan.accounting.taxes.TaxItem
import org.hildan.accounting.taxes.TaxSystem

fun TaxSystem.computeBonusTax(jetBrainsProfile: JetBrainsProfile): List<TaxItem> =
    computeBonusTax(jetBrainsProfile.profile, jetBrainsProfile.computeGrossBonus(year))

data class JetBrainsProfile(
    /**
     * Regular, common profile.
     */
    val profile: TaxProfile,
    /**
     * Performance indicators set by the manager for the year.
     */
    val performance: Performance,
) {
    fun computeGrossBonus(year: Int): Amount {
        val companyBonus = JetBrainsCompanyBonus.forYear(year)
        val coeff = performance.totalBonusCoeff(companyBonus)
        return ((profile.grossAnnualSalary / 12) * coeff).rounded()
    }
}

data class Performance(
    /**
     * Performance coefficient given by the manager for the year.
     *
     * The scale is unclear but could be described as follows:
     * * 1.0 = low performance
     * * 1.2 = good performance
     * * 1.3 = great performance beyond the regular scope of the role
     * * 1.5 = outstanding
     * * 1.7 = extremely rare case
     *
     * To award >=1.3, the manager needs to justify that the employee performed beyond their assigned scope.
     */
    val perfCoeff: Fraction,
    /**
     * Optional additive flat multiplier awarded for the release of something big, like remote development feature.
     */
    val specialReleaseFlat: Fraction = Fraction.ZERO,
) {
    /**
     * Total multiplier of the montly gross salary to get the bonus (p + (p - 1) * cm + cf + srf)
     */
    fun totalBonusCoeff(companyBonus: JetBrainsCompanyBonus) =
        perfCoeff + (perfCoeff - 1) * companyBonus.perfMultiplier + companyBonus.flatMultiplier + specialReleaseFlat
}
