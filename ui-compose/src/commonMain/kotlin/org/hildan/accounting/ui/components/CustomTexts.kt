package org.hildan.accounting.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.hildan.accounting.money.*

@Composable
fun AmountText(amount: Amount, scale: Int? = null, currencySymbol: String = "€") {
    val formattedAmount = remember(amount, scale, currencySymbol) {
        val n = if (scale == null) amount.format() else amount.format(scale)
        "$n $currencySymbol"
    }
    Text(formattedAmount)
}

@Composable
fun AbsoluteMonthText(date: AbsoluteMonth) {
    val formattedDate = remember(date) { date.toString() }
    Text(formattedDate)
}
