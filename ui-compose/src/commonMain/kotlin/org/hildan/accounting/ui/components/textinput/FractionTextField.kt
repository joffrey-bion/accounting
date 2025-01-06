package org.hildan.accounting.ui.components.textinput

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import org.hildan.accounting.money.*

@Composable
fun FractionTextField(
    value: Fraction,
    onValueChange: (Fraction) -> Unit,
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
    TypedTextField(
        value = value,
        onValueChange = {
            if (it != null) {
                onValueChange(it)
            }
        },
        adapter = FractionTextFieldAdapter,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon ?: { Icon(Icons.Default.Percent, "Percentage sign") },
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

private object FractionTextFieldAdapter : TextFieldAdapter<Fraction> {
    private val decimalRegex = Regex("""-?\d+(\.\d+)?""")

    override val keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)

    override fun toText(value: Fraction): String = value.formatPercentValue()

    override fun filter(inputText: String): String =
        inputText.filterIndexed { i, c -> c.isDigit() || (i == 0 && c == '-') || c == '.' }

    override fun parse(filteredText: String): ParseResult<Fraction> = if (decimalRegex.matches(filteredText)) {
        ParseResult.Success(filteredText.pct)
    } else {
        ParseResult.Failure("enter a valid decimal number, like 1540.78")
    }
}
