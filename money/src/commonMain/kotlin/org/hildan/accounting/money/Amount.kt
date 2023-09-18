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
    operator fun div(other: Amount) = Fraction(value / other.value)

    fun coerceAtLeast(min: Amount) = Amount(value.coerceAtLeast(min.value))

    override fun compareTo(other: Amount): Int = value.compareTo(other.value)

    override fun toString(): String = "Amount(${value.toStringExpanded()})"

    /**
     * Exposes this amount as a double value, with a potential loss of precision.
     */
    // using exactRequired=false because amounts have too much precision and the conversion fails in exact mode
    fun doubleValue() = value.doubleValue(exactRequired = false)

    /**
     * Formats this amount with the given [scale] (number of digits after the decimal point).
     */
    fun format(scale: Int = 2): String {
        val rounded = value.roundToDigitPositionAfterDecimalPoint(
            digitPosition = scale.toLong(),
            roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
        )
        val formattedRaw = rounded.toStringExpanded()
        return when (val nDigits = formattedRaw.substringAfter('.', missingDelimiterValue = "").length) {
            0 -> if (scale == 0) formattedRaw else "$formattedRaw." + "0".repeat(scale)
            in 1..<scale -> formattedRaw + "0".repeat(scale - nDigits)
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
