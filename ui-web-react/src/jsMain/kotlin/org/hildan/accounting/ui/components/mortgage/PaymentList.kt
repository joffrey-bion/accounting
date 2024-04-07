package org.hildan.accounting.ui.components.mortgage

import js.core.*
import kotlinx.datetime.*
import mui.icons.material.*
import mui.material.*
import mui.material.Box
import mui.material.Size
import mui.system.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.forms.*
import react.*
import web.cssom.*

external interface PaymentListProps : Props {
    var payments: List<Payment>?
    var onChange: ((List<Payment>) -> Unit)?
    var listProps: ListProps?
}

val PaymentList = FC<PaymentListProps> { props ->
    val payments = props.payments ?: emptyList()

    var addingNewItem by useState(false)

    mui.material.List {
        +props.listProps

        payments.sortedBy { it.date }.forEach { p ->
            ListItem {
                secondaryAction = IconButton.create {
                    onClick = {
                        props.onChange?.invoke(payments - p)
                    }
                    Delete()
                }
                ListItemText {
                    +p.date.toString()
                }
                ListItemText {
                    +"${p.amount.format(2)}€"
                }
            }
        }
        if (addingNewItem) {
            NewPaymentItem {
                initialDate = payments.lastOrNull()?.date?.plus(1, DateTimeUnit.MONTH)
                initialAmount = payments.lastOrNull()?.amount
                onSubmit = { p ->
                    addingNewItem = false
                    props.onChange?.invoke(payments + p)
                }
                onClose = { addingNewItem = false }
            }
        } else {
            ListItem {
                Button {
                    size = Size.small
                    startIcon = Add.create()
                    onClick = { addingNewItem = true }

                    +"Add payment"
                }
            }
        }
    }
}

@Suppress("INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING")
private external interface NewPaymentItemProps : Props {
    var initialDate: LocalDate?
    var initialAmount: Amount?
    var onSubmit: ((Payment) -> Unit)?
    var onClose: (() -> Unit)?
}

private val NewPaymentItem = FC<NewPaymentItemProps> { props ->
    var newItemMonth by useState(localDateStateOf(props.initialDate ?: LocalDate(2024, 1, 1)))
    var newItemAmount by useState(amountStateOf(props.initialAmount ?: 1000.eur))

    ListItem {
        Box {
            LocalDateTextField {
                value = newItemMonth
                onChange = { newItemMonth = it }
                textFieldProps = jso {
                    sx {
                        width = 10.rem
                        marginRight = 0.5.rem
                    }
                    size = Size.small
                }
            }
            AmountTextField {
                value = newItemAmount
                onChange = { newItemAmount = it }
                textFieldProps = jso {
                    sx {
                        width = 10.rem
                        marginRight = 0.5.rem
                    }
                    size = Size.small
                }
            }
        }
        secondaryAction = Box.create {
            IconButton {
                disabled = newItemMonth is TextFieldState.Invalid || newItemAmount is TextFieldState.Invalid
                onClick = {
                    val month = newItemMonth as TextFieldState.Valid
                    val amount = newItemAmount as TextFieldState.Valid
                    props.onSubmit?.invoke(Payment(month.value, amount.value))
                }
                Check()
            }
            IconButton {
                onClick = { props.onClose?.invoke() }
                Close()
            }
        }
    }
}
