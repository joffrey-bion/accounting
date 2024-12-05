package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.RepaymentSchemeDropdown
import org.hildan.accounting.ui.components.textinput.*

@Composable
fun MortgageSettingsForm(value: Mortgage, onValueChange: (Mortgage) -> Unit) {

    Column {
        LocalDateTextField(
            value = value.startDate,
            onValueChange = { onValueChange(value.copy(startDate = it)) },
            label = { Text("Start date") },
        )
        IntTextField(
            value = value.termInYears,
            onValueChange = {
                if (it != null && it > 0) {
                    onValueChange(value.copy(termInYears = it))
                }
            },
            label = { Text("Duration") },
            suffix = { Text("years") },
            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Clock icon") }
        )
        value.parts.forEachIndexed { i, part ->
            if (value.parts.size > 1) {
                Text("Part ${part.id.value}")
            }
            MortgagePartSettingsForm(
                value = part,
                onValueChange = {
                    onValueChange(value.copy(parts = value.parts.toMutableList().apply { set(i, it) }))
                },
            )
        }
        TextButton(
            onClick = {
                onValueChange(value.copy(parts = value.parts + defaultPart(value.parts.size)))
            },
        ) {
            Icon(Icons.Default.Add, "Add a mortgage part")
            Spacer(Modifier.width(8.dp))
            Text("Add mortgage part")
        }
    }
}

@Composable
private fun MortgagePartSettingsForm(
    value: MortgagePart,
    onValueChange: (MortgagePart) -> Unit,
) {
    AmountTextField(
        value = value.amount,
        onValueChange = { onValueChange(value.copy(amount = it)) },
        label = { Text("Loan amount") },
    )
    RepaymentSchemeDropdown(
        value = value.repaymentScheme,
        onValueChange = {
            onValueChange(value.copy(repaymentScheme = it))
        },
        label = { Text("Repayment scheme") },
    )
}
