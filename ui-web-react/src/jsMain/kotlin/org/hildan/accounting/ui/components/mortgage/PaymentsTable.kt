package org.hildan.accounting.ui.components.mortgage

import emotion.react.*
import js.core.*
import kotlinx.datetime.*
import mui.icons.material.*
import mui.material.*
import mui.material.Size
import mui.system.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.forms.*
import react.*
import react.dom.events.*
import react.dom.html.*
import web.cssom.*
import web.html.*
import kotlin.time.*

external interface PaymentsTableProps : TableProps {
    var payments: List<Payment>?
    var onChange: ((List<Payment>) -> Unit)?
    var tableContainerProps: TableContainerProps?
}

val PaymentsTable = FC<PaymentsTableProps>("PaymentsTable") { props ->
    val payments = props.payments ?: emptyList()

    var editingIndex by useState<Int?>(null)
    var addingNewItem by useState(false)

    TableContainer {
        +props.tableContainerProps

        Table {
            padding = TablePadding.normal
            size = Size.small

            TableBody {
                payments.sortedBy { it.date }.forEachIndexed { i, p ->
                    if (editingIndex == i) {
                        EditablePaymentRow {
                            initialPayment = p
                            onSubmit = { editedPayment ->
                                editingIndex = null
                                props.onChange?.invoke(payments - p + editedPayment)
                            }
                            onCancel = { addingNewItem = false }
                        }
                    } else {
                        PaymentRow {
                            payment = p
                            onEdit = { editingIndex = i }
                            onDelete = { props.onChange?.invoke(payments - p) }
                        }
                    }
                }
                if (addingNewItem) {
                    EditablePaymentRow {
                        initialPayment = payments.lastOrNull()?.let { it.copy(date = it.date.plus(1, DateTimeUnit.MONTH)) }
                        onSubmit = { p ->
                            addingNewItem = false
                            props.onChange?.invoke(payments + p)
                        }
                        onCancel = { addingNewItem = false }
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
    var onEdit: (() -> Unit)?
    var onDelete: (() -> Unit)?
}

private val PaymentRow = FC<PaymentRowProps>("PaymentRow") { props ->
    val payment = props.payment ?: error("missing payment prop")
    TableRow {
        TableCell {
            css {
                width = 10.rem
            }
            +payment.date.toString()
        }
        TableCell {
            align = TdAlign.right
            css {
                width = 10.rem
            }
            +"${payment.amount.format(2)}€"
        }
        TableCell {
            Box {
                css {
                    display = Display.flex
                }
                IconButton {
                    title = "Edit"
                    onClick = { props.onEdit?.invoke() }
                    Edit { fontSize = SvgIconSize.small }
                }
                IconButton {
                    title = "Remove"
                    onClick = { props.onDelete?.invoke() }
                    Close { fontSize = SvgIconSize.small }
                }
            }
        }
    }
}

private external interface EditablePaymentRowProps : Props {
    var initialPayment: Payment?
    var onSubmit: ((Payment) -> Unit)?
    var onCancel: (() -> Unit)?
}

private val EditablePaymentRow = FC<EditablePaymentRowProps>("EditablePaymentRow") { props ->
    var newItemDate by useState(localDateStateOf(props.initialPayment?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())))
    var newItemAmount by useState(amountStateOf(props.initialPayment?.amount ?: 1000.eur))

    fun submit() {
        val date = newItemDate as? TextFieldState.Valid ?: return
        val amount = newItemAmount as? TextFieldState.Valid ?: return
        props.onSubmit?.invoke(Payment(date.value, amount.value))
    }

    fun cancel() {
        props.onCancel?.invoke()
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
            LocalDateTextField {
                value = newItemDate
                onChange = { newItemDate = it }
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
                    disabled = newItemDate is TextFieldState.Invalid || newItemAmount is TextFieldState.Invalid
                    onClick = { submit() }
                    Check { fontSize = SvgIconSize.small }
                }
                IconButton {
                    title = "Cancel"
                    onClick = { cancel() }
                    Close { fontSize = SvgIconSize.small }
                }
            }
        }
    }
}
