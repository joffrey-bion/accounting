package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * An abstract representation of a dynamic interest rate, which can vary throughout the course of the mortgage.
 */
sealed interface InterestRate {

    /**
     * Gives the effective rate for the given [month] and [currentLtvRatio].
     */
    fun at(month: AbsoluteMonth, currentLtvRatio: Fraction): Fraction

    /**
     * An interest rate that doesn't depend on the moment in time.
     * It can depend on other things, like the current loan-to-value (LTV) ratio.
     */
    sealed interface TimeIndependent : InterestRate {
        override fun at(month: AbsoluteMonth, currentLtvRatio: Fraction): Fraction = at(currentLtvRatio)

        fun at(currentLtvRatio: Fraction): Fraction
    }

    /**
     * An interest rate that stays constant forever.
     */
    data class Fixed(val rate: Fraction) : TimeIndependent {
        override fun at(currentLtvRatio: Fraction) = rate
    }

    /**
     * An interest rate that changes based on the loan-to-value (LTV) ratio.
     */
    data class DynamicLtv(val sortedRates: List<RateGroup>) : TimeIndependent {

        constructor(ratesPerLtvRatio: Map<Fraction, Fraction>) : this(ratesPerLtvRatio.map { RateGroup(it.key, it.value) }
            .sortedBy { it.maxLtvRatio })

        override fun at(currentLtvRatio: Fraction) =
            sortedRates.firstOrNull { it.maxLtvRatio >= currentLtvRatio }?.rate
                ?: error("No interest rate found for LTV ratio $currentLtvRatio, max LTV is ${sortedRates.maxOf { it.maxLtvRatio }}")

        data class RateGroup(
            /**
             * The upper bound for the loan-to-value ratio of this group.
             */
            val maxLtvRatio: Fraction,
            /**
             * THe interest rate that applies for this group (below [maxLtvRatio]).
             */
            val rate: Fraction,
        )
    }

    /**
     * An interest rate that is predicted to change at some point in time.
     */
    data class Predicted(
        /**
         * The month from which the [futureRate] applies.
         */
        val changeDate: AbsoluteMonth,
        /**
         * The initial interest rate up to and excluding [changeDate].
         */
        val initialRate: TimeIndependent,
        /**
         * The interest rate from [changeDate] (included).
         */
        val futureRate: TimeIndependent,
    ): InterestRate {
        override fun at(month: AbsoluteMonth, currentLtvRatio: Fraction): Fraction = if (month < changeDate) {
            initialRate.at(month, currentLtvRatio)
        } else {
            futureRate.at(month, currentLtvRatio)
        }
    }
}
