package org.hildan.accounting.ui.components.textinput

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import kotlinx.datetime.*
import org.hildan.accounting.ui.components.datepicker.*

@Composable
fun LocalDateTextField(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        FullDatePickerDialog(
            initialValue = value,
            onConfirm = { onValueChange(it ?: error("the selected date should not be null")) },
            onDismiss = { showDatePicker = false }
        )
    }
    TypedTextField(
        value = value,
        onValueChange = {
            if (it != null) {
                onValueChange(it)
            }
        },
        adapter = LocalDateTextFieldAdapter,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon ?: {
            IconButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                onClick = { showDatePicker = true },
            ) {
                Icon(Icons.Default.EditCalendar, contentDescription = "Select date")
            }
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        keyboardActions = keyboardActions,
        singleLine = true,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

private object LocalDateTextFieldAdapter : TextFieldAdapter<LocalDate> {

    override val keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

    override fun toText(value: LocalDate): String = value.format(LocalDate.Formats.ISO)

    override fun filter(inputText: String): String = inputText.filterIndexed { _, c -> c.isDigit() || c == '-' }

    override fun parse(filteredText: String): ParseResult<LocalDate> = try {
        ParseResult.Success(LocalDate.parse(filteredText))
    } catch (e: IllegalArgumentException) {
        ParseResult.Failure("enter a valid date in ISO format, like 2024-05-23")
    }
}
