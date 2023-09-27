package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * A property (house or apartment) to be bought.
 */
data class Property(
    /**
     * The property value (also known as the [WOZ value](https://www.amsterdam.nl/en/municipal-taxes/property-valuation-woz))
     * is used to calculate how much tax you owe, and also determines the maximum that the banks can lend.
     *
     * The value of an existing house/apartment is independent of its price. The house is evaluated to determine the
     * WOZ value.
     *
     * For a new build purchase, there is no valuation of the property, and the WOZ value is usually set to the
     * purchase price, including options/extra work (and subtracting the discarded work).
     */
    val wozValue: Amount,
    /**
     * The different installments that constitute the purchase.
     * Existing houses/apartments have a single payment.
     * New builds have the land purchase and several construction bills.
     */
    val installments: List<Payment>,
) {
    /**
     * The total purchase price of this property, which is the sum of all installments.
     */
    val price: Amount = installments.sumOf { it.amount }

    companion object {
        /**
         * An existing house or apartment, bought with the given [purchase] payment, and with a given [wozValue].
         *
         * The value may be different from the price, but the bank cannot lend more than the value.
         */
        fun existing(purchase: Payment, wozValue: Amount) =
            Property(wozValue = wozValue, installments = listOf(purchase))

        /**
         * A new house or apartment that has yet to be built.
         *
         * The [installments] are the payments that need to be made for a new build:
         *  * the land purchase price, paid at the notary upon signature, after which we start paying back the loan
         *  * the construction bills, paid out from the mortgage when some construction milestone is complete.
         *    Interest is only paid on the part of the mortgage that was actually spent to pay bills.
         *
         * The WOZ value is defined to be exactly the total purchase price, include the land and construction bills.
         */
        fun newBuild(installments: List<Payment>) =
            Property(wozValue = installments.sumOf { it.amount }, installments = installments)
    }
}
