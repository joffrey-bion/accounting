package org.hildan.accounting.taxes.values

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.Fraction

internal fun taxParams(configure: NLTaxParameters.() -> Unit): NLTaxParameters = NLTaxParameters().apply(configure)

internal class NLTaxParameters {
    /** Wage tax bracket 2 lower bound (exclusive). */
    var a2_1: Amount? = null
    /** Wage tax bracket 3 lower bound (exclusive). */
    var a3_1: Amount? = null

    /** Wage tax bracket 1 rate. */
    var b1_1: Fraction? = null
    /** Wage tax bracket 2 rate. */
    var b2_1: Fraction? = null
    /** Wage tax bracket 3 rate. */
    var b3_1: Fraction? = null

    /** General tax credit maximum. */
    var ahkm1_1: Amount? = null
    /** General tax credit phase-out threshold. */
    var ahkg1: Amount? = null
    /** General tax credit phase-out rate. */
    var ahka1_1: Fraction? = null

    /** Labor tax credit (ARK) bracket 1 rate. */
    var arko1_1: Fraction? = null
    /** Labor tax credit (ARK) bracket 2 rate. */
    var arko2_1: Fraction? = null
    /** Labor tax credit (ARK) bracket 3 rate. */
    var arko3_1: Fraction? = null
    /** Labor tax credit (ARK) bracket 4 rate (which will be turned negative). */
    var arka4_1: Fraction? = null

    /** Labor tax credit (ARK) bracket 1 upper bound (inclusive) */
    var arkg1: Amount? = null
    /** Labor tax credit (ARK) bracket 2 upper bound (inclusive) */
    var arkg2: Amount? = null
    /** Labor tax credit (ARK) bracket 3 upper bound (inclusive) */
    var arkg3: Amount? = null
    /** Labor tax credit (ARK) bracket 4 upper bound (inclusive) */
    var arkg4: Amount? = null

    /** Tax rate applied to bonuses. This value is taken from a separate table. */
    var specialRewardsTaxRate: Fraction? = null
}
