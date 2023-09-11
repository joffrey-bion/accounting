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

val Int.pct: Fraction get() = Fraction(toBigDecimalForCurrencyOps(exponentModifier = -2))
val String.pct: Fraction get() = Fraction(toBigDecimalForCurrencyOps(exponentModifier = -2))
