package org.hildan.accounting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.textinput.*

@Composable
fun EditablePaymentList(
    payments: List<Payment>,
    addButtonText: String,
    onValueChange: (List<Payment>) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(payments) { index, installment ->
            EditablePaymentListItem(
                value = installment,
                onValueChange = {
                    onValueChange(payments.toMutableList().apply { set(index, it) })
                },
                onDelete = {
                    onValueChange(payments - installment)
                }
            )
        }
    }
    IconAndTextButton(
        icon = Icons.Default.Add,
        text = addButtonText,
        onClick = { onValueChange(payments + createPaymentTodayZero()) },
    )
}

private fun createPaymentTodayZero() = Payment(
    date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    amount = Amount.ZERO,
)

@Composable
private fun EditablePaymentListItem(value: Payment, onValueChange: (Payment) -> Unit, onDelete: () -> Unit) {
    ListItem(
        modifier = Modifier.width(IntrinsicSize.Min),
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val onValueChange1 = { it: Payment -> onValueChange(it) }
                LocalDateTextField(
                    value = value.date,
                    label = { Text("Date") },
                    onValueChange = { onValueChange1(value.copy(date = it)) },
                    modifier = Modifier.widthIn(max = 160.dp),
                )
                Spacer(Modifier.width(5.dp))
                AmountTextField(
                    value = value.amount,
                    label = { Text("Amount") },
                    onValueChange = { onValueChange1(value.copy(amount = it)) },
                    modifier = Modifier.width(150.dp),
                )
                Spacer(Modifier.width(5.dp))
                OutlinedTextField(
                    value = value.description,
                    label = { Text("Description") },
                    onValueChange = { onValueChange1(value.copy(description = it)) },
                    modifier = Modifier.width(400.dp),
                )
                Spacer(Modifier.width(5.dp))
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(Icons.Default.Delete, "Remove payment")
                }
            }
        },
    )
}
