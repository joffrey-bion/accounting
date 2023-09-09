package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * An abstract representation of a dynamic interest rate, which can vary throughout the course of the mortgage.
 */
sealed interface InterestRate {

    /**
     * Gives the effective rate for the given [currentLtvRatio].
     */
    fun at(currentLtvRatio: Fraction): Fraction

    /**
     * An interest rate that stays constant forever.
     */
    data class Fixed(val rate: Fraction) : InterestRate {
        override fun at(currentLtvRatio: Fraction) = rate
    }

    /**
     * An interest rate that changes based on the loan-to-value (LTV) ratio.
     */
    data class LtvAdjusted(val sortedRates: List<LtvRate>) : InterestRate {

        constructor(ratesPerLtvRatio: Map<Fraction, Fraction>) : this(ratesPerLtvRatio.map { LtvRate(it.key, it.value) }
            .sortedBy { it.loanToValueRatio })

        override fun at(currentLtvRatio: Fraction) =
            sortedRates.firstOrNull { it.loanToValueRatio >= currentLtvRatio }?.rate
                ?: error("No interest rate found for LTV ratio $currentLtvRatio, max LTV is ${sortedRates.maxOf { it.loanToValueRatio }}")

        data class LtvRate(val loanToValueRatio: Fraction, val rate: Fraction)
    }
}

