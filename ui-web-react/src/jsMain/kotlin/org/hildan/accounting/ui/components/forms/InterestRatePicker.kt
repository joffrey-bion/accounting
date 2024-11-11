package org.hildan.accounting.ui.components.forms

import emotion.react.*
import js.core.*
import mui.material.*
import mui.material.Box
import mui.material.Size
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.global.*
import react.*
import react.dom.html.*
import web.cssom.*

external interface InterestRatePickerProps : Props {
    var label: String?
    var value: InterestRate?
    var onChange: ((InterestRate) -> Unit)?
}

val InterestRatePicker = FC<InterestRatePickerProps> { props ->
    var fixedFraction by useState(percentageStateOf((props.value as? InterestRate.Fixed)?.rate ?: 3.pct))
    var dynamicLtv by useState((props.value as? InterestRate.DynamicLtv)?.sortedRates ?: listOf(InterestRate.DynamicLtv.RateGroup(80.pct, 3.pct)))

    FormControl {
        FormLabel { +(props.label ?: "Interest rate") }
        RadioGroup {
            defaultValue = when (val rate = props.value) {
                null, is InterestRate.Fixed -> "fixed"
                is InterestRate.DynamicLtv -> "dynamic-ltv"
                is InterestRate.Predicted -> rate.initialRate
            }
            onChange = { _, selectedRadio ->
                val newRate = when(selectedRadio) {
                    "fixed" -> InterestRate.Fixed((fixedFraction as TextFieldState.Valid).value)
                    "dynamic-ltv" -> InterestRate.DynamicLtv(dynamicLtv)
                    else -> error("Unknown interest rate type radio button state '$selectedRadio'")
                }
                props.onChange?.invoke(newRate)
            }
            FormControlLabel {
                control = Radio.create { size = Size.small }
                label = Box.create {
                    css {
                        display = Display.flex
                        alignItems = AlignItems.center
                    }
                    ReactHTML.span {
                        css {
                            marginRight = 1.rem
                        }
                        +"Fixed"
                    }
                    PercentageTextField {
                        textFieldProps = jso {
                            label = ReactNode("Fixed")
                            size = Size.small
                        }
                        value = fixedFraction
                        onChange = { fixedFraction = it }
                    }
                }
                value = "fixed"
            }
            Tooltip {
                title = ReactNode("An interest rate that auto-adjusts when the loan-to-value ratio changes")
                FormControlLabel {
                    control = Radio.create { size = Size.small }
                    label = Box.create {
                        css {
                            display = Display.flex
                            alignItems = AlignItems.center
                        }
                        ReactHTML.span {
                            css {
                                marginRight = 1.rem
                            }
                            +"Dynamic LTV"
                        }
                        RateGroupFields {
                            rateGroups = dynamicLtv
                            onChange = { dynamicLtv = it }
                        }
                    }
                    value = "dynamic-ltv"
                }
            }
        }
    }
}

external interface RateGroupFieldsProps : Props {
    var rateGroups: List<InterestRate.DynamicLtv.RateGroup>?
    var onChange: ((List<InterestRate.DynamicLtv.RateGroup>) -> Unit)?
}

private val RateGroupFields = FC<RateGroupFieldsProps>("RateGroupFields") { props ->
    val rateGroups = props.rateGroups ?: error("rateGroups prop missing")
    Box {
        css {
            display = Display.flex
            alignItems = AlignItems.center
        }
        rateGroups.sortedBy { it.maxLtvRatio }.forEachIndexed { index, rateGroup ->
            PercentageTextField {
                key = rateGroup.rate.formatPercentValue()
                textFieldProps = jso {
                    label = ReactNode("Rate")
                    size = Size.small
                    css {
                        width = 5.5.rem
                    }
                }
                value = TextFieldState.Valid(rateGroup.rate.formatPercentValue(), rateGroup.rate)
                onChange = {
                    if (it is TextFieldState.Valid) {
                        val newGroup = InterestRate.DynamicLtv.RateGroup(rateGroup.maxLtvRatio, it.value)
                        props.onChange?.invoke(rateGroups - rateGroup + newGroup)
                    }
                }
            }
            ReactHTML.span { +"<=" }
            PercentageTextField {
                key = rateGroup.maxLtvRatio.formatPercentValue()
                textFieldProps = jso {
                    label = ReactNode("LTV")
                    size = Size.small
                    css {
                        width = 4.5.rem
                    }
                    InputProps = jso {
                        css {
                            height = 2.rem
                        }
                    }
                }
                value = TextFieldState.Valid(rateGroup.maxLtvRatio.formatPercentValue(), rateGroup.maxLtvRatio)
                onChange = {
                    if (it is TextFieldState.Valid) {
                        val newGroup = InterestRate.DynamicLtv.RateGroup(it.value, rateGroup.rate)
                        props.onChange?.invoke(rateGroups - rateGroup + newGroup)
                    }
                }
            }
            if (index < rateGroups.lastIndex) {
                ReactHTML.span { +"<" }
            }
        }
    }
}