package org.hildan.accounting.money

/**
 * A payment made at the end of a month.
 */
data class Payment(val date: AbsoluteMonth, val amount: Amount)
