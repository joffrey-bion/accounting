package org.hildan.accounting.mortgage

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.*

/**
 * An abstract representation of a dynamic interest rate, which can vary throughout the course of the mortgage.
 */
sealed interface InterestRate {

    /**
     * Gives the effective rate for the given [date] and [currentLtvRatio].
     */
    fun at(date: LocalDate, currentLtvRatio: Fraction): Fraction

    /**
     * An interest rate that doesn't depend on the moment in time.
     * It can depend on other things, like the current loan-to-value (LTV) ratio.
     */
    sealed interface TimeIndependent : InterestRate {
        override fun at(date: LocalDate, currentLtvRatio: Fraction): Fraction = at(currentLtvRatio)

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

        constructor(ratesPerLtvRatio: Map<Fraction, Fraction>, maxLtvRate: Fraction) : this(
            sortedRates = ratesPerLtvRatio.map { RateGroup(it.key, it.value) }.sortedBy { it.maxLtvRatio }
                + RateGroup(maxLtvRatio = null, maxLtvRate),
        )

        override fun at(currentLtvRatio: Fraction) =
            sortedRates.first { it.maxLtvRatio == null || currentLtvRatio <= it.maxLtvRatio }.rate

        data class RateGroup(
            /**
             * The upper bound for the loan-to-value ratio of this group, if any.
             */
            val maxLtvRatio: Fraction?,
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
         * The date from which the [futureRate] applies.
         */
        val changeDate: LocalDate,
        /**
         * The initial interest rate up to and excluding [changeDate].
         */
        val initialRate: TimeIndependent,
        /**
         * The interest rate from [changeDate] (included).
         */
        val futureRate: TimeIndependent,
    ): InterestRate {
        override fun at(date: LocalDate, currentLtvRatio: Fraction): Fraction = if (date < changeDate) {
            initialRate.at(date, currentLtvRatio)
        } else {
            futureRate.at(date, currentLtvRatio)
        }
    }
}
