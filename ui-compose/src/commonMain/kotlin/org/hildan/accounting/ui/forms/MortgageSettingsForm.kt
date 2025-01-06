package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hildan.accounting.money.pct
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.RepaymentSchemeDropdown
import org.hildan.accounting.ui.components.textinput.*
import kotlin.collections.plus

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
    InterestRateForm(
        value = value.annualInterestRate,
        onValueChange = { onValueChange(value.copy(annualInterestRate = it)) },
    )
}

@Composable
private fun InterestRateForm(
    value: InterestRate,
    onValueChange: (InterestRate) -> Unit,
) {
    // TODO add a button to switch from Fixed to DynamicLtv
    // TODO use the UI of Fixed rate when DynamicLTV has only one group? How to deal with the upper bound?
    // TODO maybe implement Fixed as just a specical case of DynamicLtv? How to deal with the upper bound?
    when (value) {
        is InterestRate.Fixed -> FractionTextField(
            value = value.rate,
            onValueChange = { onValueChange(value.copy(rate = it)) },
            label = { Text("Annual interest rate") },
        )
        is InterestRate.DynamicLtv -> {
            DynamicLtvRateForm(value, onValueChange)
        }
        is InterestRate.Predicted -> TODO()
    }
}

@Composable
private fun DynamicLtvRateForm(
    value: InterestRate.DynamicLtv,
    onValueChange: (InterestRate) -> Unit,
) {
    value.sortedRates.forEachIndexed { i, rateGroup ->
        DynamicLtvRateGroupForm(
            rateGroup = rateGroup,
            onValueChange = {
                onValueChange(value.copy(sortedRates = value.sortedRates.withReplacedItem(index = i, newItem = it)))
            },
            onDelete = {
                onValueChange(value.copy(sortedRates = value.sortedRates - rateGroup))
            },
            index = i,
            nGroups = value.sortedRates.size,
        )
    }
    TextButton(
        onClick = {
            val currentLast = value.sortedRates.last()
            onValueChange(value.copy(sortedRates = value.sortedRates + currentLast.copy(maxLtvRatio = currentLast.maxLtvRatio + 10.pct)))
        },
    ) {
        Icon(Icons.Default.Add, "Add LTV group")
        Spacer(Modifier.width(8.dp))
        Text("Add LTV group")
    }
}

@Composable
private fun DynamicLtvRateGroupForm(
    rateGroup: InterestRate.DynamicLtv.RateGroup,
    onValueChange: (InterestRate.DynamicLtv.RateGroup) -> Unit,
    onDelete: () -> Unit,
    index: Int,
    nGroups: Int,
) {
    Row(
        modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FractionTextField(
            modifier = Modifier.weight(1.2f),
            value = rateGroup.rate,
            onValueChange = { onValueChange(rateGroup.copy(rate = it)) },
            label = { Text("Annual rate $index") },
        )
        Spacer(Modifier.width(8.dp))
        FractionTextField(
            modifier = Modifier.weight(1f),
            value = rateGroup.maxLtvRatio,
            onValueChange = {
                onValueChange(rateGroup.copy(maxLtvRatio = it))
            },
            label = { Text("≤ LTV ratio") },
        )
        IconButton(
            onClick = onDelete,
            enabled = nGroups > 1,
        ) {
            Icon(Icons.Default.Delete, "Remove LTV group")
        }
    }
}

private fun <T> List<T>.withReplacedItem(index: Int, newItem: T): List<T> =
    toMutableList().apply { set(index, newItem) }
