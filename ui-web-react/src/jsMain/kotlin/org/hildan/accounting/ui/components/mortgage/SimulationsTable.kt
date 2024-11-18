package org.hildan.accounting.ui.components.mortgage

import mui.material.*
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.mortgage.SimulationResult
import react.FC
import react.Props
import react.dom.aria.ariaLabel

external interface SimulationsTableProps : Props {
    var simulations: List<SimulationResult>
}

val SimulationsTable = FC<SimulationsTableProps>("SimulationsTable") { props ->
    TableContainer {
        component = Paper

        Table {
            ariaLabel = "A comparison of mortgage simulations"

            TableHead {
                TableRow {
                    TableCell { +"Sim name" }
                    TableCell { +"Total loan" }
                    TableCell { +"Own funds" }
                    TableCell { +"Total interest" }
                    TableCell { +"Avg pay" }
                    TableCell { +"Max pay" }
                    TableCell { +"99p pay" }
                    TableCell { +"95p pay" }
                    TableCell { +"90p pay" }
                }
            }

            TableBody {
                props.simulations.forEach { s ->
                    TableRow {
                        key = s.name

                        TableCell { +s.name }
                        TableCell { +s.mortgageAmount.format(scale = 0) }
                        TableCell { +s.ownFunds.format(scale = 0) }
                        TableCell { +s.totalInterest.format(2) }
                        TableCell { +(s.monthSummaries.sumOf { it.totalCollected } / s.monthSummaries.size).format(2) }
                        TableCell { +s.annuitiesDistribution.max.format(2) }
                        TableCell { +s.annuitiesDistribution.p99.format(2) }
                        TableCell { +s.annuitiesDistribution.p95.format(2) }
                        TableCell { +s.annuitiesDistribution.p90.format(2) }
                    }
                }
            }
        }
    }
}
