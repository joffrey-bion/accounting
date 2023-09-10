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
