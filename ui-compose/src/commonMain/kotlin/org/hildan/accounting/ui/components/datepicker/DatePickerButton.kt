package org.hildan.accounting.ui.components.datepicker

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import kotlin.time.*

private fun LocalDate.toDatePickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

private fun Long.fromDatePickerMillisToLocalDate() =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

@Composable
fun DatePickerButton(
    value: LocalDate? = null,
    onValueChange: (LocalDate?) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showDatePicker = true },
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(text = value?.format(LocalDate.Formats.ISO) ?: "Select")
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.EditCalendar, "Select date")
    }

    if (showDatePicker) {
        FullDatePickerDialog(
            initialValue = value,
            onConfirm = { onValueChange(it) },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FullDatePickerDialog(
    initialValue: LocalDate?,
    onConfirm: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialValue?.toDatePickerMillis(),
        initialDisplayMode = DisplayMode.Picker,
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onConfirm(datePickerState.selectedDateMillis?.fromDatePickerMillisToLocalDate())
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}
