package org.hildan.accounting.ui.components.mortgage

import js.core.*
import kotlinx.datetime.*
import mui.material.*
import mui.material.Box
import mui.system.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property
import org.hildan.accounting.ui.components.forms.*
import org.hildan.accounting.ui.global.*
import react.*
import react.dom.*
import react.dom.events.*
import web.cssom.*
import web.html.*

external interface SimulationSettingsDialogProps : Props {
    var prefilledData: SimulationSettings?
    var onCreate: ((SimulationSettings) -> Unit)?
    var onCancel: (() -> Unit)?
    var dialogProps: DialogProps?
}

private val defaultStartDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
private val defaultConfig = SimulationSettings(
    simulationName = "My Simulation",
    mortgage = Mortgage(
        startDate = defaultStartDate,
        termInYears = 30,
        dayCountConvention = DayCountConvention.ThirtyE360,
        parts = listOf(
            MortgagePart(
                id = MortgagePartId("Part1"),
                amount = 400_000.eur,
                annualInterestRate = InterestRate.Fixed(Fraction("0.04")),
            )
        )
    ),
    property = Property.Existing(purchase = Payment(defaultStartDate, 420_000.eur), wozValue = 400_000.eur),
)

val SimulationSettingsDialog = FC<SimulationSettingsDialogProps> { props ->
    val initialSettings = props.prefilledData ?: defaultConfig

    var simName by useState(initialSettings.simulationName)
    var mortgageAmount by useState(amountStateOf(initialSettings.mortgage.amount))
    var mortgageInterestRate by useState(initialSettings.mortgage.parts.first().annualInterestRate)
    var mortgageTerm by useState(intTextFieldStateOf(initialSettings.mortgage.termInYears))
    var mortgageStartMonth by useState(localDateStateOf(initialSettings.mortgage.startDate))
    var extraRedemptions by useState(initialSettings.mortgage.parts.first().extraPayments)

    val isValid = simName.isNotEmpty() || //
        mortgageAmount is TextFieldState.Valid || //
        mortgageTerm is TextFieldState.Valid || //
        mortgageStartMonth is TextFieldState.Valid

    Dialog {
        +props.dialogProps

        DialogTitle {
            +"New simulation"
        }
        DialogContent {
            TextField {
                commonTextFieldProps()
                label = ReactNode("Simulation name")
                value = simName
                onChange = { simName = it.unsafeCast<ChangeEvent<HTMLInputElement>>().target.value }
            }
            Box {
                AmountTextField {
                    textFieldProps = jso {
                        commonTextFieldProps()
                        label = ReactNode("Amount")
                    }
                    value = mortgageAmount
                    onChange = { mortgageAmount = it }
                }
                IntTextField {
                    textFieldProps = jso {
                        commonTextFieldProps()
                        label = ReactNode("Duration")
                        InputProps = jso {
                            endAdornment = InputAdornment.create {
                                position = InputAdornmentPosition.end
                                +"years"
                            }
                        }
                    }
                    value = mortgageTerm
                    onChange = { mortgageTerm = it }
                }
                // maybe use a date picker when props are correct
                LocalDateTextField {
                    textFieldProps = jso {
                        commonTextFieldProps()
                        label = ReactNode("Start month")
                    }
                    value = mortgageStartMonth
                    onChange = { mortgageStartMonth = it }
                }
            }
            Box {
                sx {
                    marginTop = 1.rem
                }

                InterestRatePicker {
                    value = mortgageInterestRate
                    onChange = { mortgageInterestRate = it }
                }
            }
            Box {
                sx {
                    marginTop = 1.rem
                }

                FormControl {
                    FormLabel { +"Planned voluntary repayments" }
                    PaymentsTable {
                        tableContainerProps = jso {
                            sx {
                                maxWidth = 30.rem
                            }
                        }
                        payments = extraRedemptions
                        onChange = { extraRedemptions = it }
                    }
                }
            }
        }
        DialogActions {
            Button {
                onClick = { props.onCancel?.invoke() }
                +"Cancel"
            }
            Button {
                disabled = !isValid
                onClick = {
                    // TODO we should build entirely new settings once the form covers everything
                    val settings = initialSettings.copy(
                        simulationName = simName,
                        mortgage = initialSettings.mortgage.copy(
                            startDate = mortgageStartMonth.valueOrThrow(),
                            termInYears = mortgageTerm.valueOrThrow(),
                            parts = listOf(
                                initialSettings.mortgage.parts[0].copy(
                                    amount = mortgageAmount.valueOrThrow(),
                                    extraPayments = extraRedemptions,
                                )
                            ),
                        )
                    )
                    props.onCreate?.invoke(settings)
                }
                +"Create"
            }
        }
    }
}

private fun TextFieldProps.commonTextFieldProps() {
    sx {
        width = 10.rem
        marginRight = 0.5.rem
    }
    margin = FormControlMargin.normal
}

private fun <T> TextFieldState<T>.valueOrThrow(): T = when (this) {
    is TextFieldState.Invalid -> error("Invalid state: $validationMessage")
    is TextFieldState.Valid -> value
}
