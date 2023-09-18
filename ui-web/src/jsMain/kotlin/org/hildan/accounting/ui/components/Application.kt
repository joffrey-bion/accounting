package org.hildan.accounting.ui.components

import io.data2viz.geom.*
import org.hildan.accounting.money.*
import org.hildan.accounting.ui.*
import react.*

val Application = FC("Application") {
    val simulation80 = simulateMortgage(simName = "80% LTV", amount = totalPrice * 80.pct)
    val simulation700k = simulateMortgage(simName = "700k", amount = 700_000.eur)
    val simulation90 = simulateMortgage(simName = "90% LTV", amount = totalPrice * 90.pct)

    val chartSize = Size(500.0, 400.0)

    MonthlyPaymentsChart {
        size = chartSize
        simulation = simulation80
    }
    MonthlyPaymentsChart {
        size = chartSize
        simulation = simulation700k
    }
    MonthlyPaymentsChart {
        size = chartSize
        simulation = simulation90
    }
}
