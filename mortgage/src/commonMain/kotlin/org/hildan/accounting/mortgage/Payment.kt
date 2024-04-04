package org.hildan.accounting.mortgage

import org.hildan.accounting.money.*

/**
 * A dated payment.
 *
 * It can represent any money transfer at the beginning or end of a month (it's unspecified by this class, and has to
 * be inferred from the context)
 */
data class Payment(val date: AbsoluteMonth, val amount: Amount)
