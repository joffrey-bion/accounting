package org.hildan.accounting.ui.components.mortgage

import mui.material.*
import org.hildan.accounting.mortgage.SimulationResult
import org.hildan.accounting.ui.components.AmountTableCell
import org.hildan.accounting.ui.components.HeaderTableCell
import react.FC
import react.dom.aria.ariaLabel

external interface MonthlyTableProps : TableContainerProps {
    var simulation: SimulationResult
}

val MonthlyTable = FC<MonthlyTableProps> { props ->
    TableContainer {
        +props

        component = Paper

        Table {
            stickyHeader = true
            size = Size.small
            ariaLabel = "A monthly payments table for the simulation named '${props.simulation.name}'"

            TableHead {
                TableRow {
                    HeaderTableCell {
                        header = "Month"
                    }
                    HeaderTableCell {
                        header = "Balance"
                        tooltip = "The mortgage balance at the beginning of the month (before the payment)"
                    }
                    HeaderTableCell {
                        header = "Redemption"
                        tooltip = "The part of the payment that actually pays back the mortgage, and is subtracted " +
                            "from the mortgage balance (which reduces future interest)."
                    }
                    HeaderTableCell {
                        header = "Interest"
                        tooltip = "The interest paid to the bank for borrowing the money, proportional to the " +
                            "mortgage balance over the last period (from the last payment until now)."
                    }
                    HeaderTableCell {
                        header = "Total"
                        tooltip = "The total amount (redemption + interest) that is actually paid to the bank."
                    }
                }
            }

            TableBody {
                props.simulation.monthlyPayments.forEach { p ->
                    TableRow {
                        key = p.date.toString()

                        TableCell { +p.date.toString() }
                        AmountTableCell {
                            amount = p.balanceBefore
                            scale = 0
                        }
                        AmountTableCell {
                            amount = p.principalReduction
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.interest
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.total
                            scale = 2
                        }
                    }
                }
            }
        }
    }
}
