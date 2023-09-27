package org.hildan.accounting.ui.components.forms

import js.core.*
import mui.material.*
import org.hildan.accounting.money.*
import org.hildan.accounting.ui.global.*
import react.*

fun amountStateOf(amount: Amount): TextFieldState<Amount> = TextFieldState.Valid(amount.format(), amount)

val AmountTextField = FC<TypedTextFieldProps<Amount>>("AmountTextField") { props ->
    validatedTypedTextField(props, convert = ::convertToAmount) {
        InputProps = jso {
            endAdornment = InputAdornment.create {
                position = InputAdornmentPosition.end
                +"€"
            }
        }
    }
}

private fun convertToAmount(value: String) = try {
    ConversionResult.Success(Amount(value))
} catch (e: Exception) {
    ConversionResult.Failure("Enter a valid number")
}
