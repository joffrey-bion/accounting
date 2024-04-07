package org.hildan.accounting.ui.components.forms

import js.core.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import mui.icons.material.*
import mui.material.*
import org.hildan.accounting.ui.global.*
import react.*

fun localDateStateOf(date: LocalDate): TextFieldState<LocalDate> =
    TextFieldState.Valid(date.format(LocalDate.Formats.ISO), date)

// TODO replace with DatePicker with only month and year views: <DatePicker views={['year', 'month']}/>
// https://mui.com/x/react-date-pickers/date-picker/#views
val LocalDateTextField = FC<TypedTextFieldProps<LocalDate>>("LocalDateTextField") { props ->
    validatedTypedTextField(props, convert = ::convertToLocalDate) {
        placeholder = "yyyy-MM"
        InputProps = jso {
            endAdornment = InputAdornment.create {
                position = InputAdornmentPosition.end
                CalendarMonth()
            }
        }
    }
}

private fun convertToLocalDate(value: String): ConversionResult<LocalDate> {
    return try {
        ConversionResult.Success(LocalDate.parse(value))
    } catch (e: IllegalArgumentException) {
        ConversionResult.Failure("Enter a year and month in ISO format 'yyyy-MM-dd'")
    }
}
