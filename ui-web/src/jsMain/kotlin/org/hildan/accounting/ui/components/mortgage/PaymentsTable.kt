package org.hildan.accounting.ui.components.mortgage

import js.core.*
import mui.icons.material.*
import mui.material.*
import mui.material.Box
import mui.material.Size
import mui.system.*
import org.hildan.accounting.money.*
import org.hildan.accounting.ui.components.forms.*
import react.*
import react.dom.events.*
import react.dom.html.*
import web.cssom.*
import web.html.*

external interface PaymentsTableProps : TableProps {
    var payments: List<Payment>?
    var onChange: ((List<Payment>) -> Unit)?
    var tableContainerProps: TableContainerProps?
}

val PaymentsTable = FC<PaymentsTableProps>("PaymentsTable") { props ->
    val payments = props.payments ?: emptyList()

    var addingNewItem by useState(false)

    TableContainer {
        +props.tableContainerProps

        Table {
            padding = TablePadding.normal

            TableBody {
                payments.sortedBy { it.date }.forEach { p ->
                    PaymentRow {
                        payment = p
                        onDelete = { props.onChange?.invoke(payments - p) }
                    }
                }
                if (addingNewItem) {
                    NewPaymentRow {
                        initialMonth = payments.lastOrNull()?.date?.plusMonths(1)
                        initialAmount = payments.lastOrNull()?.amount
                        onSubmit = { p ->
                            addingNewItem = false
                            println("About to add element p")
                            val paymentsPlusP = payments + p
                            println("Added p")
                            props.onChange?.invoke(paymentsPlusP)
                        }
                        onClose = { addingNewItem = false }
                    }
                } else {
                    TableRow {
                        TableCell {
                            colSpan = 3

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
        }
    }
}

private external interface PaymentRowProps : Props {
    var payment: Payment?
    var onDelete: (() -> Unit)?
}

private val PaymentRow = FC<PaymentRowProps>("PaymentRow") { props ->
    val payment = props.payment ?: error("missing payment prop")
    TableRow {
        TableCell {
            sx {
                width = 10.rem
            }
            +payment.date.toString()
        }
        TableCell {
            align = TdAlign.right
            sx {
                width = 10.rem
            }
            +"${payment.amount.format(2)}€"
        }
        TableCell {
            IconButton {
                title = "Remove"
                onClick = { props.onDelete?.invoke() }
                Delete()
            }
        }
    }
}

@Suppress("INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING")
private external interface NewPaymentRowProps : Props {
    var initialMonth: AbsoluteMonth?
    var initialAmount: Amount?
    var onSubmit: ((Payment) -> Unit)?
    var onClose: (() -> Unit)?
}

private val NewPaymentRow = FC<NewPaymentRowProps>("NewPaymentRow") { props ->
    var newItemMonth by useState(absoluteMonthStateOf(props.initialMonth ?: AbsoluteMonth(2024, 1)))
    var newItemAmount by useState(amountStateOf(props.initialAmount ?: 1000.eur))

    fun submit() {
        val month = newItemMonth as? TextFieldState.Valid ?: return
        val amount = newItemAmount as? TextFieldState.Valid ?: return
        props.onSubmit?.invoke(Payment(month.value, amount.value))
    }

    fun cancel() {
        props.onClose?.invoke()
    }

    fun handleKeyPress(event: KeyboardEvent<HTMLDivElement>) {
        when (event.key) {
            "Enter" -> submit()
            "Escape" -> cancel()
        }
    }

    TableRow {
        TableCell {
            sx {
                width = 10.rem
            }
            AbsoluteMonthTextField {
                value = newItemMonth
                onChange = { newItemMonth = it }
                textFieldProps = jso {
                    size = Size.small
                    onKeyDown = { handleKeyPress(it) }
                }
            }
        }
        TableCell {
            sx {
                width = 10.rem
            }
            AmountTextField {
                value = newItemAmount
                onChange = { newItemAmount = it }
                textFieldProps = jso {
                    size = Size.small
                    onKeyDown = { handleKeyPress(it) }
                }
            }
        }
        TableCell {
            Box {
                sx {
                    display = Display.flex
                }
                IconButton {
                    title = "Add"
                    disabled = newItemMonth is TextFieldState.Invalid || newItemAmount is TextFieldState.Invalid
                    onClick = { submit() }
                    Check()
                }
                IconButton {
                    title = "Cancel"
                    onClick = { cancel() }
                    Close()
                }
            }
        }
    }
}