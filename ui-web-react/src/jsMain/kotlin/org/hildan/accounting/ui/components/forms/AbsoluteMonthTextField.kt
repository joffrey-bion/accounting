package org.hildan.accounting.ui.components.forms

import js.core.*
import mui.icons.material.*
import mui.material.*
import org.hildan.accounting.money.*
import org.hildan.accounting.ui.global.*
import react.*

fun absoluteMonthStateOf(month: AbsoluteMonth): TextFieldState<AbsoluteMonth> =
    TextFieldState.Valid(month.toString(), month)

// TODO replace with DatePicker with only month and year views: <DatePicker views={['year', 'month']}/>
// https://mui.com/x/react-date-pickers/date-picker/#views
val AbsoluteMonthTextField = FC<TypedTextFieldProps<AbsoluteMonth>>("AbsoluteMonthTextField") { props ->
    validatedTypedTextField(props, convert = ::convertToAbsoluteMonth) {
        placeholder = "yyyy-MM"
        InputProps = jso {
            endAdornment = InputAdornment.create {
                position = InputAdornmentPosition.end
                CalendarMonth()
            }
        }
    }
}

private val absoluteMonthRegex = Regex("""(\d{4})-(\d{2})""")

private fun convertToAbsoluteMonth(value: String): ConversionResult<AbsoluteMonth> {
    val match = absoluteMonthRegex.matchEntire(value)
        ?: return ConversionResult.Failure("Enter a year and month in the form 'yyyy-MM'")

    val year = match.groupValues[1].toInt()
    val month = match.groupValues[2].toInt()
    if (month !in 1..12) {
        return ConversionResult.Failure("The month must be between 01 and 12")
    }
    return ConversionResult.Success(AbsoluteMonth(year, month))
}
