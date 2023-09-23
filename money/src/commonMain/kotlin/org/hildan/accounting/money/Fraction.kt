package org.hildan.accounting.money

import com.ionspin.kotlin.bignum.decimal.*
import kotlin.jvm.*

/**
 * A fraction (ratio or percentage) of something.
 */
@JvmInline
value class Fraction internal constructor(internal val value: BigDecimal) : Comparable<Fraction> {

    constructor(value: Int) : this(value.toBigDecimalForCurrencyOps())
    constructor(value: String) : this(value.toBigDecimalForCurrencyOps())

    operator fun unaryMinus() = Fraction(-value)
    operator fun plus(other: Fraction) = Fraction(value + other.value)
    operator fun minus(other: Fraction) = Fraction(value - other.value)
    operator fun minus(other: Int) = Fraction(value - other.toBigDecimalForCurrencyOps())
    operator fun times(other: Fraction) = Fraction(value * other.value)

    override fun compareTo(other: Fraction): Int = value.compareTo(other.value)

    fun formatRate(): String = value.toStringExpanded()
    fun formatPercent(): String = (value * 100).toStringExpanded()

    override fun toString(): String = formatPercent() + "%"

    companion object {
        val ZERO = Fraction("0")
    }
}

val Int.pct: Fraction get() = Fraction(toBigDecimalForCurrencyOps(exponentModifier = -2))
val String.pct: Fraction get() = Fraction(toBigDecimalForCurrencyOps(exponentModifier = -2))
