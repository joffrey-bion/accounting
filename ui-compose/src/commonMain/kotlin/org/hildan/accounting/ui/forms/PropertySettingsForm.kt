package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.*
import org.hildan.accounting.ui.components.textinput.*

@Composable
fun PropertySettingsForm(value: Property, onValueChange: (Property) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = value is Property.NewConstruction,
                onCheckedChange = { isNewConstruction ->
                    check(isNewConstruction != (value is Property.NewConstruction)) { "Type is expected to have changed" }
                    val newProp = when (value) {
                        is Property.NewConstruction -> value.toExisting()
                        is Property.Existing -> value.toNewConstruction()
                    }
                    onValueChange(newProp)
                },
            )
            Spacer(Modifier.width(10.dp))
            Text("New construction")
            Spacer(Modifier.width(4.dp))
            InfotipBubble("Check this if the house/apartment is not built yet")
        }
        when (value) {
            is Property.NewConstruction -> {
                AmountTextField(
                    value = value.wozValue,
                    onValueChange = { onValueChange(value.copy(wozValue = it)) },
                    label = { Text("WOZ Value") },
                    supportingText = { Text("For a new construction, this is usually defined as the total price.") }
                )
                EditablePaymentList(
                    payments = value.installments,
                    onValueChange = { onValueChange(value.copy(installments = it)) },
                    modifier = Modifier.wrapContentWidth(),
                )
            }
            is Property.Existing -> {
                AmountTextField(
                    value = value.purchase.amount,
                    onValueChange = { onValueChange(value.copy(purchase = value.purchase.copy(amount = it))) },
                    label = { Text("Purchase price") },
                )
                AmountTextField(
                    value = value.wozValue,
                    onValueChange = { onValueChange(value.copy(wozValue = it)) },
                    label = { Text("Estimated WOZ value") },
                )
            }
        }
    }
}

private fun Property.Existing.toNewConstruction() = Property.NewConstruction(
    installments = listOf(purchase),
    wozValue = wozValue,
)

private fun Property.NewConstruction.toExisting() = Property.Existing(
    purchase = Payment(date = installments.first().date, amount = installments.sumOf { it.amount }),
    wozValue = wozValue,
)
