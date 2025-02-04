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
                // TODO hide this initially, and offer to override it optionally
                AmountTextField(
                    value = value.wozValue,
                    onValueChange = { onValueChange(value.copy(wozValue = it)) },
                    label = { Text("WOZ Value") },
                    supportingText = { Text("For a new construction, this is usually defined as the total price.") }
                )
                AmountTextField(
                    value = value.initialNotaryPayment.amount,
                    onValueChange = {
                        onValueChange(value.copy(initialNotaryPayment = value.initialNotaryPayment.copy(amount = it)))
                    },
                    label = { Text("Initial notary payment") },
                    supportingText = {
                        Text(
                            "The amount paid at the notary when signing the deed. " +
                                "This includes everything that is not covered by the construction account " +
                                "(land price, development costs, advisor fees, notary fees, tanslation fees, etc.)"
                        )
                    }
                )
                EditablePaymentList(
                    payments = value.constructionInstallments,
                    onValueChange = { onValueChange(value.copy(constructionInstallments = it)) },
                    modifier = Modifier.wrapContentWidth().heightIn(max = 500.dp),
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
    initialNotaryPayment = purchase,
    constructionInstallments = emptyList(),
    wozValue = wozValue,
)

private fun Property.NewConstruction.toExisting() = Property.Existing(
    purchase = Payment(
        date = initialNotaryPayment.date,
        amount = initialNotaryPayment.amount + constructionInstallments.sumOf { it.amount },
    ),
    wozValue = wozValue,
)
