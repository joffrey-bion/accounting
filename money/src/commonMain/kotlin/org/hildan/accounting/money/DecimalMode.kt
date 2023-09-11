package org.hildan.accounting.money

import com.ionspin.kotlin.bignum.decimal.*

/**
 * A [DecimalMode] suitable for all operations with currency.
 *
 * We can't rely on the default (exact) decimal mode because it has [RoundingMode.NONE], and this doesn't support
 * operations that have an infinite representation in decimal (e.g. 1/3 = 0.3333...), which throw [ArithmeticException].
 *
 * Therefore, we need to specify a way to stop via some kind of rounding, and either a limited decimal precision
 * (total number of significant digits) or a limited scale (number of digits after the decimal point) to define *where*
 * to stop.
 *
 * Providing only a scale doesn't work (see https://github.com/ionspin/kotlin-multiplatform-bignum/issues/267),
 * so we instead specify a rather large precision to support big amounts or big numbers of decimal places.
 */
internal val CurrencyOpsDecimalMode = DecimalMode(decimalPrecision = 30, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

internal fun Int.toBigDecimalForCurrencyOps(exponentModifier: Long? = null) =
    toBigDecimal(exponentModifier, decimalMode = CurrencyOpsDecimalMode)

internal fun Long.toBigDecimalForCurrencyOps(exponentModifier: Long? = null) =
    toBigDecimal(exponentModifier, decimalMode = CurrencyOpsDecimalMode)

internal fun String.toBigDecimalForCurrencyOps(exponentModifier: Long? = null) =
    toBigDecimal(exponentModifier, decimalMode = CurrencyOpsDecimalMode)
