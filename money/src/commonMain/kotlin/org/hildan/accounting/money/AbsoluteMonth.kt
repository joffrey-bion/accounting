package org.hildan.accounting.money

/**
 * Represents a local date with month precision, so basically a month and a year.
 */
data class AbsoluteMonth(val year: Int, val month: Int) : Comparable<AbsoluteMonth> {

    /**
     * Returns the absolute month coming after this one.
     */
    fun next() = plusMonths(1)

    /**
     * Returns the absolute month corresponding to [n] months after this one.
     */
    fun plusMonths(n: Int) = AbsoluteMonth(
        year = year + (month - 1 + n) / 12,
        month = (month - 1 + n) % 12 + 1,
    )

    override fun compareTo(other: AbsoluteMonth): Int = compareValuesBy(this, other) { it.toString() }

    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}"
}
