package org.hildan.accounting.ui.components.mortgage

import mui.material.*
import org.hildan.accounting.mortgage.SimulationResult
import org.hildan.accounting.ui.components.AmountTableCell
import org.hildan.accounting.ui.components.HeaderTableCell
import react.FC
import react.dom.aria.ariaLabel

external interface AnnualTableProps : TableContainerProps {
    var simulation: SimulationResult
    var tableContainerProps: TableContainerProps?
    var tableProps: TableProps?
}

val AnnualTable = FC<AnnualTableProps> { props ->
    TableContainer {
        component = Paper

        +props.tableContainerProps

        Table {
            stickyHeader = true
            size = Size.small
            ariaLabel = "A table describing payments year-by-year for the simulation named '${props.simulation.name}'"

            +props.tableProps

            TableHead {
                TableRow {
                    HeaderTableCell {
                        header = "Year"
                    }
                    HeaderTableCell {
                        header = "Num. Months"
                        tooltip = "The number of months where a mortgage payment happened during the year. It is 12 " +
                            "for most years, but the very first and last year of the simulation are usually partial " +
                            "unless the mortgage started in January."
                    }
                    HeaderTableCell {
                        header = "Starting balance"
                        tooltip = "The mortgage balance at the beginning of the year (before the first payment)"
                    }
                    HeaderTableCell {
                        header = "Total Redemption"
                        tooltip = "The total redemption from all payments during the year. The redemption is the part" +
                            " of the payments that actually pays back the mortgage, and is subtracted from the " +
                            "mortgage balance (which reduces future interest)."
                    }
                    HeaderTableCell {
                        header = "Total Interest"
                        tooltip = "The interest paid to the bank for borrowing the money, proportional to the " +
                            "mortgage balance based on the interest rate applied over each period."
                    }
                    HeaderTableCell {
                        header = "Total Payments"
                        tooltip = "The total amount (redemption + interest) that is actually paid to the bank."
                    }
                    HeaderTableCell {
                        header = "Avg. Redemption"
                        tooltip = "The average monthly redemption for the year. The redemption is the part of the " +
                            "payments that actually pays back the mortgage, and is subtracted from the mortgage " +
                            "balance (which reduces future interest)."
                    }
                    HeaderTableCell {
                        header = "Avg. Interest"
                        tooltip = "The average monthly interest for the year. The interest is the cost paid to the " +
                            "bank for borrowing the money, and depends on the interest rate."
                    }
                    HeaderTableCell {
                        header = "Avg. Payment"
                        tooltip = "The average monthly payment (redemption + interest) for the year."
                    }
                }
            }

            TableBody {
                props.simulation.yearSummaries.forEach { p ->
                    TableRow {
                        key = p.year.toString()

                        TableCell {
                            align = TableCellAlign.center
                            +p.year.toString()
                        }
                        TableCell {
                            align = TableCellAlign.center
                            +p.nMonths.toString()
                        }
                        AmountTableCell {
                            amount = p.balanceBefore
                            scale = 0
                        }
                        AmountTableCell {
                            amount = p.principalReduction
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.effectiveInterest
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.totalPayments
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.avgMonthlyPrincipalReduction
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.avgMonthlyInterest
                            scale = 2
                        }
                        AmountTableCell {
                            amount = p.avgMonthlyPayment
                            scale = 2
                        }
                    }
                }
            }
        }
    }
}
