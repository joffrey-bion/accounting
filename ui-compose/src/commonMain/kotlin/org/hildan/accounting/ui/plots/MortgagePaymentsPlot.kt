package org.hildan.accounting.ui.plots

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import io.github.koalaplot.core.line.*
import io.github.koalaplot.core.style.*
import io.github.koalaplot.core.util.*
import io.github.koalaplot.core.xygraph.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun MortgagePaymentsPlot(simulationResult: SimulationResult, modifier: Modifier) {
    val payments = simulationResult.monthSummaries
    val xAxisModel = remember(payments) {
        localDateAxisModel(
            min = payments.minOf { it.date },
            max = payments.maxOf { it.date },
        )
    }
    val yAxisModel = remember(payments) {
        amountAxisModel(max = payments.maxOf { it.totalCollected + 100.eur })
    }
    val entries = remember(payments) {
        payments.map { stackedAreaPlotEntry(it.date, arrayOf(it.totalCollected, it.mortgagePayment.principalReduction)) }
    }
    val styles = List(2) {
        StackedAreaStyle(
            LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 1.dp),
            AreaStyle(brush = SolidColor(Color.Blue.copy(alpha = 0.20f * (it + 1)))),
        )
    }
    XYGraph(
        xAxisModel = xAxisModel,
        yAxisModel = yAxisModel,
        modifier = modifier,
        xAxisTitle = "Month",
        yAxisTitle = "Mortgage Payments",
        xAxisLabels = { it.format(LocalDate.Formats.ISO) },
        yAxisLabels = { "${it.format()} €" },
    ) {
        StackedAreaPlot(
            data = entries,
            styles = styles,
            firstBaseline = AreaBaseline.ConstantLine(Amount.ZERO),
        )
    }
}

