package org.hildan.accounting.mortgage

import kotlinx.datetime.*
import org.hildan.accounting.money.*

/**
 * A payment of [amount] made at [date].
 */
data class Payment(val date: LocalDate, val amount: Amount, val description: String = "")
