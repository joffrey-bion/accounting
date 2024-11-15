package org.hildan.accounting.money

/**
 * A distribution of amounts of money.
 */
data class Distribution internal constructor(
    /**
     * The maximum value in this distribution.
     */
    val max: Amount,
    /**
     * The 99th percentile. 99% of the amounts in this distribution are less than or equal to this amount.
     */
    val p99: Amount,
    /**
     * The 95th percentile. 95% of the amounts in this distribution are less than or equal to this amount.
     */
    val p95: Amount,
    /**
     * The 90th percentile. 90% of the amounts in this distribution are less than or equal to this amount.
     */
    val p90: Amount,
    /**
     * The average amount.
     */
    val average: Amount,
)

/**
 * Calculates the distribution of this collection of amounts.
 */
fun Iterable<Amount>.distribution(): Distribution {
    val sorted = sortedDescending()
    return Distribution(
        max = sorted.first(),
        p99 = sorted[nthPercentile(99, sorted.size)],
        p95 = sorted[nthPercentile(95, sorted.size)],
        p90 = sorted[nthPercentile(90, sorted.size)],
        average = sorted.sum() / sorted.size,
    )
}

/**
 * The number of elements that represent the given percentile [p] for a sample of the given [size].
 */
private fun nthPercentile(p: Int, size: Int) = size * (100 - p) / 100
