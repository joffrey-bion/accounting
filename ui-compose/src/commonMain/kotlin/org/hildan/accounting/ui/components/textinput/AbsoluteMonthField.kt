package org.hildan.accounting.ui.components.textinput

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*

@Composable
fun AbsoluteMonthField(
    value: AbsoluteMonth,
    onValueChange: (AbsoluteMonth) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
) {
    var currentText by remember(value) { mutableStateOf(value.toString()) }
    val isInvalidDate = remember(currentText) { currentText.toAbsoluteMonth() == null }
    OutlinedTextField(
        value = currentText,
        onValueChange = { text: String ->
            val filtered = text.filter { c -> c.isDigit() || c == '-' }
            currentText = filtered
            filtered.toAbsoluteMonth()?.let {
                onValueChange(it)
            }
        },
        modifier = Modifier.width(150.dp).then(modifier),
        label = label,
        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
        isError = isInvalidDate,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

private fun String.toAbsoluteMonth(): AbsoluteMonth? {
    if (count { it == '-' } != 1) {
        return null
    }
    val (yearText, monthText) = split("-")
    val year = yearText.toIntOrNull() ?: return null
    val month = monthText.toIntOrNull()?.takeIf { it in 1..12 } ?: return null
    return AbsoluteMonth(
        year = year,
        month = month,
    )
}
