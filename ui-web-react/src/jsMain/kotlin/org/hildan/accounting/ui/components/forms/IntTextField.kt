package org.hildan.accounting.ui.components.forms

import react.*

fun intTextFieldStateOf(value: Int): TextFieldState<Int> = TextFieldState.Valid(value.toString(), value)

val IntTextField = FC<TypedTextFieldProps<Int>>("IntTextField") { props ->
    validatedTypedTextField(props, convert = {
        it.toIntOrNull()?.let { ConversionResult.Success(it) } ?: ConversionResult.Failure("Enter a valid number")
    })
}
