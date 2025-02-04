package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.*
import org.hildan.accounting.ui.components.textinput.*
import org.hildan.accounting.ui.utils.*

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
                    onValueChange(value.copy(parts = value.parts.withReplacedItem(index = i, newItem = it)))
                },
            )
        }
        IconAndTextButton(
            icon = Icons.Default.Add,
            text = "Add a mortgage part",
            onClick = {
                onValueChange(value.copy(parts = value.parts + defaultPart(value.parts.size)))
            },
        )
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
    InterestRateForm(
        value = value.annualInterestRate,
        onValueChange = { onValueChange(value.copy(annualInterestRate = it)) },
    )
}
