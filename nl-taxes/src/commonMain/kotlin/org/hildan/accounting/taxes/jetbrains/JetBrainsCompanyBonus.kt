package org.hildan.accounting.taxes.jetbrains

import org.hildan.accounting.money.Fraction

data class JetBrainsCompanyBonus(
    /**
     * Company multiplier for the year (same for everyone at JetBrains), which multiplies the part of the perf
     * coefficient that is above 1.
     */
    val perfMultiplier: Fraction,
    /**
     * Company flat bonus for the year (same for everyone at JetBrains), which is added to the global multiplier.
     */
    val flatMultiplier: Fraction,
) {
    companion object {
        fun forYear(year: Int) = when (year) {
            2021 -> JetBrainsCompanyBonus(perfMultiplier = Fraction(3), flatMultiplier = Fraction("0.4"))
            2022 -> JetBrainsCompanyBonus(perfMultiplier = Fraction(2), flatMultiplier = Fraction("0.3"))
            else -> error("Unknown JetBrains company bonus multipliers")
        }
    }
}
