package org.hildan.accounting.taxes.brackets

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.Fraction
import org.hildan.accounting.money.eur

/**
 * A set of brackets, which can apply different rates to different parts of an amount of money.
 */
interface BracketSet {

    /**
     * Applies this set of brackets to the given [amount]. The rate of each bracket is applied to the
     * corresponding part of the given amount, and the results are returned as [AppliedBracket]s.
     */
    fun applyTo(amount: Amount): List<AppliedBracket>
}

/**
 * The result of applying a bracket to an amount.
 */
data class AppliedBracket(
    /**
     * The portion of the amount that falls into the bracket.
     */
    val amountInBracket: Amount,
    /**
     * The result of applying the bracket rate to [amountInBracket].
     */
    val result: Amount,
    /**
     * Information about the bracket definition.
     */
    val bracket: Bracket,
)

/**
 * Represents a bracket.
 */
data class Bracket(val rate: Fraction, val range: AmountRange)

/**
 * A half-open range of amounts.
 */
data class AmountRange(
    val minExclusive: Amount,
    val maxInclusive: Amount?,
) {
    override fun toString(): String = if (maxInclusive == null) {
        "]${minExclusive.format(scale = 0)}, ∞["
    } else {
        "]${minExclusive.format(scale = 0)}, ${maxInclusive.format(scale = 0)}]"
    }
}

/**
 * Builds a [BracketSet].
 */
internal fun buildBrackets(configure: BracketsBuilder.() -> Unit): BracketSet =
    BracketsBuilderImpl().apply(configure).build()

internal interface BracketsBuilder {
    /**
     * Defines a new bracket with the given [rate] going from the previous threshold (exclusive) until the [upTo]
     * amount (inclusive). If there is no previous bracket, this new bracket starts at 0.
     */
    fun bracket(rate: Fraction, upTo: Amount)
    /**
     * Defines a last bracket with the given [rate] going from the previous threshold (exclusive) until infinity.
     * If there is no previous bracket, this new bracket starts at 0.
     */
    fun lastBracket(rate: Fraction)
}

private class BracketsBuilderImpl : BracketsBuilder {
    private var nextLow = 0.eur
    private var lastBracketSpecified = false
    private val brackets = mutableListOf<Bracket>()

    override fun bracket(rate: Fraction, upTo: Amount) {
        check(!lastBracketSpecified) { "The last bracket was already specified, cannot add more" }
        require(nextLow < upTo) { "upper bound must be above last threshold ($nextLow)" }
        brackets.add(Bracket(rate, range = AmountRange(nextLow, maxInclusive = upTo)))
        nextLow = upTo
    }

    override fun lastBracket(rate: Fraction) {
        check(!lastBracketSpecified) { "The last bracket was already specified, cannot add more" }
        brackets.add(Bracket(rate, range = AmountRange(nextLow, maxInclusive = null)))
        lastBracketSpecified = true
    }

    fun build(): BracketSet = BracketSetImpl(brackets)
}

private class BracketSetImpl(val brackets: List<Bracket>) : BracketSet {
    override fun applyTo(amount: Amount): List<AppliedBracket> = brackets.map { it.applyTo(amount) }
}

private fun Bracket.applyTo(amount: Amount): AppliedBracket {
    val amountInBracket = amount.partIn(range)
    return AppliedBracket(
        amountInBracket = amountInBracket,
        result = amountInBracket * rate,
        bracket = this,
    )
}

private fun Amount.partIn(range: AmountRange): Amount {
    val capped = if (range.maxInclusive == null) this else coerceAtMost(range.maxInclusive)
    return (capped - range.minExclusive).coerceAtLeast(Amount.ZERO)
}
