package org.hildan.accounting.money

import com.ionspin.kotlin.bignum.decimal.*
import kotlin.jvm.*

/**
 * An amount of money in an undefined (but assumed known) currency.
 */
// the BigDecimal constructor is private as we don't want consumers to have to know about decimal modes
@JvmInline
value class Amount private constructor(private val value: BigDecimal) : Comparable<Amount> {

    constructor(value: Int) : this(value.toBigDecimalForCurrencyOps())
    constructor(value: Long) : this(value.toBigDecimalForCurrencyOps())
    constructor(value: String) : this(value.toBigDecimalForCurrencyOps())

    operator fun unaryMinus() = Amount(-value)
    operator fun plus(other: Amount) = Amount(value + other.value)
    operator fun minus(other: Amount) = Amount(value - other.value)
    operator fun times(quantity: Int) = Amount(value * quantity.toBigDecimalForCurrencyOps())
    operator fun times(fraction: Fraction) = Amount(value * fraction.value)
    operator fun div(quantity: Int) = Amount(value / quantity.toBigDecimalForCurrencyOps())
    operator fun div(fraction: Fraction) = Amount(value / fraction.value)
    operator fun div(other: Amount) = Fraction(value / other.value)

    /**
     * Returns a copy of this amount rounded to the nearest integer, rounding ties away from zero.
     */
    fun rounded(roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO) =
        roundedToScale(scale = 0, roundingMode)

    /**
     * Returns a copy of this amount rounded to 2 decimal points to have a whole number of cents, rounding ties away
     * from zero.
     */
    fun roundedToTheCent(roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO) =
        roundedToScale(scale = 2, roundingMode)

    /**
     * Returns a copy of this amount rounded to a number of digits after the decimal point equal to [scale], rounding
     * ties away from zero.
     */
    fun roundedToScale(scale: Int, roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO) =
        Amount(value.roundToDigitPositionAfterDecimalPoint(
            digitPosition = scale.toLong(),
            roundingMode = roundingMode,
        ))

    override fun compareTo(other: Amount): Int = value.compareTo(other.value)

    override fun toString(): String = "Amount(${value.toStringExpanded()})"

    /**
     * Exposes this amount as a double value, with a potential loss of precision.
     */
    // using exactRequired=false because amounts have too much precision and the conversion fails in exact mode
    fun doubleValue() = value.doubleValue(exactRequired = false)

    /**
     * Exposes this amount as a float value, with a potential loss of precision.
     */
    // using exactRequired=false because amounts have too much precision and the conversion fails in exact mode
    fun floatValue() = value.floatValue(exactRequired = false)

    /**
     * Formats this amount as a string without any rounding.
     */
    fun format(): String = value.toStringExpanded()

    /**
     * Formats this amount rounded to the given [scale] (number of digits after the decimal point).
     */
    fun format(scale: Int): String = roundedToScale(scale).formatWithMinScale(scale)

    /**
     * Formats this amount as a string without any rounding, but ensuring a [minScale] number of digits after the
     * decimal point (padding with 0s as needed).
     */
    fun formatWithMinScale(minScale: Int): String {
        val formattedRaw = format()
        return when (val nDigits = formattedRaw.substringAfter('.', missingDelimiterValue = "").length) {
            0 -> if (minScale == 0) formattedRaw else "$formattedRaw." + "0".repeat(minScale)
            in 1..<minScale -> formattedRaw + "0".repeat(minScale - nDigits)
            else -> formattedRaw
        }
    }

    companion object {
        val ZERO = Amount(0.toBigDecimalForCurrencyOps())
    }
}

fun <T> Iterable<T>.sumOf(selector: (T) -> Amount): Amount = fold(Amount.ZERO) { sum, e -> sum + selector(e) }

fun Iterable<Amount>.sum(): Amount = fold(Amount.ZERO, Amount::plus)

fun Int.toAmount(): Amount = Amount(this)
fun Long.toAmount(): Amount = Amount(this)
fun String.toAmount(): Amount = Amount(this)

val Int.eur: Amount get() = toAmount()
val Long.eur: Amount get() = toAmount()
val String.eur: Amount get() = toAmount()
