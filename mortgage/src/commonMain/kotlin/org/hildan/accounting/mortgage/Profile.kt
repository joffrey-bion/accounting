package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

data class Profile(
    /**
     * The property being bought.
     */
    val property: Property,
    /**
     * The payments made voluntarily to pay back the loan, usually to reduce the interest and thus the monthly payments.
     */
    val extraRedemptions: List<Payment>,
)

/**
 * A payment made at the end of a month.
 *
 * This could be a complete purchase, or construction bill (for new constructions), a voluntary redemption payback...
 */
data class Payment(val date: AbsoluteMonth, val amount: Amount)
