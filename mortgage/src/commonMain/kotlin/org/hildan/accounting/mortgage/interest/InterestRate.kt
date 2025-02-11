package org.hildan.accounting.mortgage.interest

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.*

/**
 * An abstract representation of a dynamic interest rate, which can vary throughout the course of the mortgage.
 */
sealed interface InterestRate {
    /**
     * The day count convention to apply with this interest rate.
     */
    val dayCountConvention: DayCountConvention

    /**
     * Gives the effective rate for the given [date] and [currentLtvRatio].
     */
    fun at(date: LocalDate, currentLtvRatio: Fraction): ApplicableInterestRate

    /**
     * An interest rate that doesn't depend on the moment in time.
     * It can depend on other things, like the current loan-to-value (LTV) ratio.
     */
    sealed interface TimeIndependent : InterestRate {
        override fun at(date: LocalDate, currentLtvRatio: Fraction): ApplicableInterestRate = at(currentLtvRatio)

        fun at(currentLtvRatio: Fraction): ApplicableInterestRate
    }

    /**
     * An interest rate that stays constant forever.
     */
    data class Fixed(
        val rate: Fraction,
        override val dayCountConvention: DayCountConvention,
    ) : TimeIndependent {
        private val applicableInterestRate = ApplicableInterestRate(rate, dayCountConvention)

        override fun at(currentLtvRatio: Fraction) = applicableInterestRate
    }

    /**
     * An interest rate that changes based on the loan-to-value (LTV) ratio.
     */
    data class DynamicLtv(
        val sortedRates: List<RateGroup>,
        override val dayCountConvention: DayCountConvention,
    ) : TimeIndependent {

        constructor(
            ratesPerLtvRatio: Map<Fraction, Fraction>,
            maxLtvRate: Fraction,
            dayCountConvention: DayCountConvention,
        ) : this(
            sortedRates = ratesPerLtvRatio.map { RateGroup(it.key, it.value) }.sortedBy { it.maxLtvRatio }
                + RateGroup(maxLtvRatio = null, maxLtvRate),
            dayCountConvention = dayCountConvention,
        )

        override fun at(currentLtvRatio: Fraction) = ApplicableInterestRate(
            annualRate = sortedRates.first { it.maxLtvRatio == null || currentLtvRatio <= it.maxLtvRatio }.rate,
            dayCountConvention = dayCountConvention,
        )

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

        init {
            check(initialRate.dayCountConvention == futureRate.dayCountConvention) {
                "Day-count convention must be the same for the initial and future interest rates"
            }
        }

        override val dayCountConvention: DayCountConvention = initialRate.dayCountConvention

        override fun at(date: LocalDate, currentLtvRatio: Fraction): ApplicableInterestRate = if (date < changeDate) {
            initialRate.at(date, currentLtvRatio)
        } else {
            futureRate.at(date, currentLtvRatio)
        }
    }
}
