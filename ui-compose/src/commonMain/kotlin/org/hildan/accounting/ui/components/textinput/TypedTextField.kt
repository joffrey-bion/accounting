package org.hildan.accounting.ui.components.textinput

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*

interface TextFieldAdapter<T> {
    /**
     * Keyboard options for the text field.
     */
    val keyboardOptions: KeyboardOptions

    /**
     * Converts a typed value into the corresponding text value shown in the text field.
     */
    fun toText(value: T): String

    /**
     * Filters the given [inputText] to only keep valid characters for this type of text field.
     */
    fun filter(inputText: String): String

    /**
     * Parses the given [filteredText] into a typed value.
     */
    fun parse(filteredText: String): ParseResult<T>
}

sealed interface ParseResult<out T> {
    data class Success<T>(val value: T) : ParseResult<T>
    data class Failure(val errorDescription: String) : ParseResult<Nothing>

    companion object {
        fun <T : Any> ofNullable(value: T?, messageIfNull: String): ParseResult<T> {
            return value?.let { Success(value = it) } ?: Failure(messageIfNull)
        }
    }
}

/**
 * An adapted [OutlinedTextField] for types other than strings.
 *
 * @param value the value to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. The converted value is
 * passed as argument to the function if the input text is valid, otherwise the argument is null.
 * @param adapter the [TextFieldAdapter] to use to convert text to typed values
 * @param modifier the [Modifier] to be applied to this text field
 * @param enabled controls the enabled state of this text field. When `false`, this component will
 * not respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param readOnly controls the editable state of the text field. When `true`, the text field cannot
 * be modified. However, a user can focus it and copy text from it. Read-only text fields are
 * usually used to display pre-filled forms that a user cannot edit.
 * @param textStyle the style to be applied to the input text. Defaults to [LocalTextStyle].
 * @param label the optional label to be displayed inside the text field container. The default
 * text style for internal [Text] is [Typography.bodySmall] when the text field is in focus and
 * [Typography.bodyLarge] when the text field is not in focus
 * @param placeholder the optional placeholder to be displayed when the text field is in focus and
 * the input text is empty. The default text style for internal [Text] is [Typography.bodyLarge]
 * @param leadingIcon the optional leading icon to be displayed at the beginning of the text field
 * container
 * @param trailingIcon the optional trailing icon to be displayed at the end of the text field
 * container
 * @param prefix the optional prefix to be displayed before the input text in the text field
 * @param suffix the optional suffix to be displayed after the input text in the text field
 * @param supportingText the optional supporting text to be displayed below the text field
 * @param isError indicates if the text field's current value is in error. If set to true, the
 * label, bottom indicator and trailing icon by default will be displayed in error color
 * @param keyboardActions when the input service emits an IME action, the corresponding callback
 * is called. Note that this IME action may be different from what you specified in
 * [KeyboardOptions.imeAction]
 * @param singleLine when `true`, this text field becomes a single horizontally scrolling text field
 * instead of wrapping onto multiple lines. The keyboard will be informed to not show the return key
 * as the [ImeAction]. Note that [maxLines] parameter will be ignored as the maxLines attribute will
 * be automatically set to 1.
 * @param maxLines the maximum height in terms of maximum number of visible lines. It is required
 * that 1 <= [minLines] <= [maxLines]. This parameter is ignored when [singleLine] is true.
 * @param minLines the minimum height in terms of minimum number of visible lines. It is required
 * that 1 <= [minLines] <= [maxLines]. This parameter is ignored when [singleLine] is true.
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this text field. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this text field in different states.
 * @param shape defines the shape of this text field's border
 * @param colors [TextFieldColors] that will be used to resolve the colors used for this text field
 * in different states. See [OutlinedTextFieldDefaults.colors].
 */
@Composable
fun <T : Any> TypedTextField(
    value: T?,
    onValueChange: (T?) -> Unit,
    adapter: TextFieldAdapter<T>,
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
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var textValue by remember(value) { mutableStateOf(value?.let { adapter.toText(it) } ?: "") }

    // we don't validate initially, because we don't want red everywhere when showing the textfield for the first time
    var errorMessage by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = textValue,
        onValueChange = { inputText ->
            val filteredText = adapter.filter(inputText)
            textValue = filteredText
            when (val parseResult = adapter.parse(filteredText)) {
                is ParseResult.Success -> {
                    errorMessage = null
                    onValueChange(parseResult.value)
                }
                is ParseResult.Failure -> {
                    errorMessage = parseResult.errorDescription
                    onValueChange(null)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = errorMessage?.let {{ supportingText?.invoke() ?: Text(it) }} ?: supportingText,
        isError = isError || errorMessage != null,
        keyboardOptions = adapter.keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}
