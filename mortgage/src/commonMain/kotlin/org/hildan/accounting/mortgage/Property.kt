package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * A property (house or apartment) to be bought.
 */
sealed class Property {
    /**
     * The property value (known as the [Waardering Onroerende Zaken value](https://www.amsterdam.nl/en/municipal-taxes/property-valuation-woz),
     * or WOZ value in short) is used to calculate how much tax you owe, and also determines the maximum that
     * the banks can lend.
     *
     * The value of an existing house/apartment is independent of its price. The house is evaluated to determine the
     * WOZ value. The WOZ value of a house is public and can be found on
     * [wozwaardeloket.nl](https://www.wozwaardeloket.nl/).
     *
     * For a new build purchase, there is no valuation of the property, and the WOZ value is usually set to the
     * purchase price, including options/extra work (and subtracting the discarded work).
     */
    abstract val wozValue: Amount

    /**
     * An existing house or apartment, bought with the given [purchase] payment, and with a given estimated [wozValue].
     *
     * The value may be different from the price, but the bank cannot lend more than the value.
     */
    data class Existing(
        val purchase: Payment,
        override val wozValue: Amount,
    ) : Property()

    /**
     * A new house or apartment that has yet to be built.
     */
    data class NewConstruction(
        /**
         * This is the initial payment made at the notary on the day of the purchase.
         *
         * It includes everything except what goes to the construction account:
         *
         * - land price, development costs, land leasehold advance payment (Erfpachtcanon)
         * - first VvE contribution
         * - mortgage advisor fees
         * - notary fees (mortgage act, cohabitation agreement if applicable, etc.)
         * - registration fees to declare the mortgage at the Kadaster
         * - registration fees to declare the cohabitation agreement to the Centraal Testamenten Register (if applicable)
         * - translation fees (if a translator was hired for the procedure)
         *
         * The construction bills, additional construction options (meerwerk), and other delayed purchases (like a
         * potential parking space) go to the construction account and are not paid at the notary.
         */
        val initialNotaryPayment: Payment,
        /**
         * The installments paid from the construction account during the construction.
         *
         * The construction contract defines a list of construction milestones (e.g. "inner cavity walls are complete",
         * or "roof is watertight") and a corresponding amount that is due when this milestone is complete.
         *
         * In addition to this, the [constructionInstallments] also include additional construction options (meerwerk),
         * and other delayed purchases like a potential parking space purchase.
         */
        val constructionInstallments: List<Payment>,
        /**
         * The property value (known as the [Waardering Onroerende Zaken value](https://www.amsterdam.nl/en/municipal-taxes/property-valuation-woz),
         * or WOZ value in short) is used to calculate how much tax you owe, and also determines the maximum that
         * the banks can lend.
         *
         * For a new build purchase, there is no valuation of the property, and the WOZ value is usually set to the
         * total purchase price, including options/extra work (and subtracting the discarded work).
         */
        override val wozValue: Amount = initialNotaryPayment.amount + constructionInstallments.sumOf { it.amount },
    ) : Property()
}
