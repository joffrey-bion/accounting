package org.hildan.accounting.ui.components.forms

import js.core.*
import mui.material.*
import org.hildan.accounting.money.*
import org.hildan.accounting.ui.global.*
import react.*

fun percentageStateOf(amount: Fraction): TextFieldState<Fraction> = TextFieldState.Valid(amount.formatPercent(), amount)

val PercentageTextField = FC<TypedTextFieldProps<Fraction>>("PercentageTextField") { props ->
    validatedTypedTextField(props, convert = ::convertToPercentage) {
        InputProps = jso {
            endAdornment = InputAdornment.create {
                position = InputAdornmentPosition.end
                +"%"
            }
        }
    }
}

private fun convertToPercentage(value: String) = try {
    ConversionResult.Success(value.pct)
} catch (e: Exception) {
    ConversionResult.Failure("Enter a valid number")
}
