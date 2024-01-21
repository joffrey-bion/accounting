package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.textinput.*

@Composable
fun MortgageSettingsForm(value: Mortgage, onValueChange: (Mortgage) -> Unit) {
    Column {
        AmountTextField(
            value = value.amount,
            onValueChange = { onValueChange(value.copy(amount = it)) },
            label = { Text("Loan amount") },
        )
        IntTextField(
            value = value.nYears,
            onValueChange = {
                if (it != null && it > 0) {
                    onValueChange(value.copy(nYears = it))
                }
            },
            label = { Text("Duration") },
            suffix = { Text("years") },
            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Clock icon") }
        )
    }
}
