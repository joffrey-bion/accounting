package org.hildan.accounting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.textinput.*

@Composable
fun EditablePaymentList(
    payments: List<Payment>,
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
            )
        }
    }
}

@Composable
private fun EditablePaymentListItem(value: Payment, onValueChange: (Payment) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.width(IntrinsicSize.Min),
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditing) {
                    PaymentListItemFields(
                        initialValue = value,
                        onEditCommit = { isEditing = false; onValueChange(it) },
                        onEditCancel = { isEditing = false },
                    )
                } else {
                    AbsoluteMonthText(value.date)
                    Spacer(Modifier.width(5.dp))
                    AmountText(value.amount)
                    Spacer(Modifier.width(3.dp))
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        },
    )
}

@Composable
private fun PaymentListItemFields(
    initialValue: Payment,
    onEditCommit: (Payment) -> Unit,
    onEditCancel: () -> Unit,
) {
    var date by remember(initialValue) { mutableStateOf(initialValue.date) }
    var amount by remember(initialValue) { mutableStateOf(initialValue.amount) }
    AbsoluteMonthField(
        value = date,
        onValueChange = { date = it },
        modifier = Modifier.width(150.dp),
    )
    AmountTextField(
        value = amount,
        onValueChange = { amount = it },
        modifier = Modifier.width(150.dp),
    )
    IconButton(onClick = { onEditCommit(Payment(date = date, amount = amount)) }) {
        Icon(Icons.Default.Check, contentDescription = "Confirm")
    }
    IconButton(onClick = onEditCancel) {
        Icon(Icons.Default.Close, contentDescription = "Cancel")
    }
}
