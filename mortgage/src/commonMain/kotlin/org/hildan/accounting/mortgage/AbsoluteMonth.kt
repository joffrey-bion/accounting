package org.hildan.accounting.mortgage

/**
 * Represents a local date with month precision, so basically a month and a year.
 * 
 * The [month] is the 1-based month number in the year: 1 for January, 2 for February, etc.
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
