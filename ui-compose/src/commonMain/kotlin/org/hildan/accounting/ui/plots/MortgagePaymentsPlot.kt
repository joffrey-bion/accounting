package org.hildan.accounting.ui.plots

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import io.github.koalaplot.core.line.*
import io.github.koalaplot.core.style.*
import io.github.koalaplot.core.util.*
import io.github.koalaplot.core.xygraph.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun MortgagePaymentsPlot(simulationResult: SimulationResult) {
    val payments = simulationResult.monthlyPayments
    val xAxisModel = remember(payments) {
        val months = payments.map { it.date }
        absoluteMonthAxisModel(min = months.min(), max = months.max())
    }
    val yAxisModel = remember(payments) {
        amountAxisModel(max = payments.maxOf { it.total + 100.eur })
    }

    val entries = remember(payments) { payments.map { stackedAreaPlotEntry(it.date, arrayOf(it.total, it.principalReduction)) } }
    val styles = List(2) {
        StackedAreaStyle(
            LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 1.dp),
            AreaStyle(brush = SolidColor(Color.Blue.copy(alpha = 0.20f * (it + 1))))
        )
    }
    XYGraph(
        xAxisModel = xAxisModel,
        yAxisModel = yAxisModel,
        xAxisTitle = "Month",
        yAxisTitle = "Mortgage Payments",
        xAxisLabels = AbsoluteMonth::toString,
        yAxisLabels = { "${it.format()} €" },
    ) {
        StackedAreaPlot(
            data = entries,
            styles = styles,
            firstBaseline = AreaBaseline.ConstantLine(Amount.ZERO),
        )
    }
}

