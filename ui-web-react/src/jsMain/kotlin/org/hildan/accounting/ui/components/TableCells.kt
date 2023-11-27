package org.hildan.accounting.ui.components

import emotion.react.css
import mui.material.TableCell
import mui.material.TableCellAlign
import mui.material.TableCellProps
import mui.material.Tooltip
import org.hildan.accounting.money.Amount
import react.FC
import react.Props
import react.ReactNode
import web.cssom.FontWeight

external interface HeaderTableCellProps : Props {
    var header: String?
    var tooltip: String?
}

val HeaderTableCell = FC<HeaderTableCellProps>("HeaderTableCell") { props ->
    Tooltip {
        title = ReactNode(props.tooltip ?: "")

        TableCell {
            align = TableCellAlign.center
            css {
                fontWeight = FontWeight.bold
            }

            +props

            +props.header
        }
    }
}

external interface AmountTableCellProps : TableCellProps {
    @Suppress("INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING")
    var amount: Amount?
    var scale: Int?
    var tooltip: String?
}

val AmountTableCell = FC<AmountTableCellProps>("AmountTableCell") { props ->
    Tooltip {
        title = ReactNode(props.tooltip ?: "")

        TableCell {
            align = TableCellAlign.right
            +props

            +props.amount?.format(scale = props.scale ?: 2)
        }
    }
}
