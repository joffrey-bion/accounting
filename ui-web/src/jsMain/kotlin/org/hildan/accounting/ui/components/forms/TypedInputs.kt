package org.hildan.accounting.ui.components.forms

import mui.material.*
import react.*
import react.dom.*
import react.dom.events.*
import web.html.*

external interface TypedTextFieldProps<T> : Props {
    var value: TextFieldState<T>?
    var onChange: ((TextFieldState<T>) -> Unit)?
    var textFieldProps: TextFieldProps?
}

sealed class TextFieldState<out T> {
    abstract val textValue: String

    data class Valid<T>(override val textValue: String, val value: T) : TextFieldState<T>()
    data class Invalid(override val textValue: String, val validationMessage: String) : TextFieldState<Nothing>()
}

internal fun <T> ChildrenBuilder.validatedTypedTextField(
    props: TypedTextFieldProps<T>,
    convert: (String) -> ConversionResult<T>,
    configure: TextFieldProps.() -> Unit = {},
) {
    TextField {
        configure()
        +props.textFieldProps
        value = props.value?.textValue
        onChange = props.onChange?.let { handler ->
            { event ->
                val value = event.unsafeCast<ChangeEvent<HTMLInputElement>>().target.value
                val newState = value.toState { convert(it) }
                handler(newState)
            }
        }
        (props.value as? TextFieldState.Invalid)?.let { state ->
            error = true
            helperText = ReactNode(state.validationMessage)
        }
    }
}

internal sealed class ConversionResult<out T> {
    data class Success<T>(val value: T) : ConversionResult<T>()
    data class Failure(val validationMessage: String) : ConversionResult<Nothing>()
}

private fun <T> String.toState(convert: (String) -> ConversionResult<T>) = when (val converted = convert(this)) {
    is ConversionResult.Success -> TextFieldState.Valid(this, converted.value)
    is ConversionResult.Failure -> TextFieldState.Invalid(this, converted.validationMessage)
}
