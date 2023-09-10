package org.hildan.accounting.money

import com.ionspin.kotlin.bignum.decimal.*
import kotlin.jvm.*

/**
 * A fraction (ratio or percentage) of something.
 */
@JvmInline
value class Fraction internal constructor(internal val value: BigDecimal) : Comparable<Fraction> {
    override fun compareTo(other: Fraction): Int = value.compareTo(other.value)

    override fun toString(): String = (value * 100).toStringExpanded() + "%"
}

/**
 * An amount of money in an undefined (but assumed known) currency.
 */
@JvmInline
value class Amount internal constructor(private val value: BigDecimal) : Comparable<Amount> {
    operator fun unaryMinus() = Amount(-value)
    operator fun plus(other: Amount) = Amount(value + other.value)
    operator fun minus(other: Amount) = Amount(value - other.value)
    operator fun times(quantity: Int) = Amount(value * quantity.toBigDecimalForCurrency())
    operator fun times(fraction: Fraction) = Amount(value * fraction.value)
    operator fun div(quantity: Int) = Amount(value / quantity.toBigDecimalForCurrency())
    operator fun div(other: Amount) = Fraction(value / other.value)

    fun coerceAtLeast(min: Amount) = Amount(value.coerceAtLeast(min.value))

    override fun compareTo(other: Amount): Int = value.compareTo(other.value)

    override fun toString(): String = "Amount(${value.toStringExpanded()})"

    fun format(nDigitsAfterDot: Int = 2): String {
        val rounded = value.roundToDigitPositionAfterDecimalPoint(
            digitPosition = nDigitsAfterDot.toLong(),
            roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
        )
        val formattedRaw = rounded.toStringExpanded()
        return when (val nDigits = formattedRaw.substringAfter('.', missingDelimiterValue = "").length) {
            0 -> if (nDigitsAfterDot == 0) formattedRaw else "$formattedRaw." + "0".repeat(nDigitsAfterDot)
            in 1..<nDigitsAfterDot -> formattedRaw + "0".repeat(nDigitsAfterDot - nDigits)
            else -> formattedRaw
        }
    }

    companion object {
        val ZERO = Amount(0.toBigDecimalForCurrency())
    }
}

fun <T> Iterable<T>.sumOf(selector: (T) -> Amount): Amount = fold(Amount.ZERO) { sum, e -> sum + selector(e) }

fun Iterable<Amount>.sum(): Amount = fold(Amount.ZERO, Amount::plus)

fun Int.toAmount(): Amount = Amount(toBigDecimalForCurrency())
fun Long.toAmount(): Amount = Amount(toBigDecimalForCurrency())
fun String.toAmount(): Amount = Amount(toBigDecimalForCurrency())

val Int.eur: Amount get() = toAmount()
val Long.eur: Amount get() = toAmount()
val String.eur: Amount get() = toAmount()

val Int.pct: Fraction get() = Fraction(toBigDecimalForCurrency(exponentModifier = -2))
val String.pct: Fraction get() = Fraction(toBigDecimalForCurrency(exponentModifier = -2))

private val currencyMode = DecimalMode(decimalPrecision = 30, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale = 4)

private fun Any.toBigDecimalForCurrency(exponentModifier: Long? = null) = when (this) {
    is Byte -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is Short -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is Int -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is Long -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is Float -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is Double -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    is String -> toBigDecimal(exponentModifier, decimalMode = currencyMode)
    else -> throw IllegalArgumentException("Unsupported type ${this::class.simpleName} for BigDecimal conversion")
}
