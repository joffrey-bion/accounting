package org.hildan.accounting.ui.plots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.line.*
import io.github.koalaplot.core.style.*
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.*
import io.github.koalaplot.core.xygraph.*
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.*
import org.hildan.accounting.ui.utils.currentTextColor
import kotlin.math.*

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
    var pointerLocation: Point<LocalDate, Amount>? by remember { mutableStateOf(null) }
    val pointedMonthSummary by remember(pointerLocation) {
        derivedStateOf {
            pointerLocation?.let { pl ->
                payments.findClosestPaymentToDate(pl.x)
            }
        }
    }
    val middleDate by remember(payments) {
        derivedStateOf { payments[payments.size / 2].date }
    }
    XYGraph(
        xAxisModel = xAxisModel,
        yAxisModel = yAxisModel,
        modifier = modifier,
        xAxisTitle = "Month",
        yAxisTitle = "Mortgage Payments",
        xAxisLabels = { it.format(LocalDate.Formats.ISO) },
        yAxisLabels = { "${it.format()} €" },
        onPointerMove = { date, amount -> pointerLocation = Point(date, amount) },
    ) {
        StackedAreaPlot(
            data = entries,
            styles = styles,
            firstBaseline = AreaBaseline.ConstantLine(Amount.ZERO),
        )
        pointedMonthSummary?.let { ms ->
            VerticalLineAnnotation(
                location = ms.date,
                lineStyle = LineStyle(
                    brush = SolidColor(currentTextColor()),
                    strokeWidth = 1.dp,
                ),
            )
            XYAnnotation(
                location = Point(ms.date, pointerLocation!!.y),
                anchorPoint = if (ms.date < middleDate) AnchorPoint.BottomLeft else AnchorPoint.BottomRight,
                modifier = Modifier.offset(x = if (ms.date < middleDate) 3.dp else (-3).dp, y = (-5).dp),
                content = { MortgageMonthSummaryTooltip(ms) },
            )
        }
    }
}

private fun List<MortgageMonthSummary>.findClosestPaymentToDate(date: LocalDate): MortgageMonthSummary =
    minBy { abs(it.date.daysUntil(date)) }

@Composable
private fun MortgageMonthSummaryTooltip(monthSummary: MortgageMonthSummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(2.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = "Payment date:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                LocalDateText(monthSummary.date)
            }
            Row {
                Text(text = "Total:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                AmountText(monthSummary.totalCollected)
            }
            Row(modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min)) {
                VerticalDivider(
                    color = currentTextColor(),
                    modifier = Modifier.padding(start = 30.dp, end = 15.dp).fillMaxHeight(),
                )
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                    Text("Repayment:")
                    Text("Interest:")
                    if (monthSummary.deductedConstructionInterest > Amount.ZERO) {
                        Text("BD interest:")
                    }
                    if (monthSummary.mortgagePayment.extraPrincipalReduction > Amount.ZERO) {
                        Text("Extra\u00a0payments:")
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    AmountText(monthSummary.mortgagePayment.principalReduction)
                    AmountText(monthSummary.mortgagePayment.interest)
                    if (monthSummary.deductedConstructionInterest > Amount.ZERO) {
                        AmountText(monthSummary.deductedConstructionInterest)
                    }
                    if (monthSummary.mortgagePayment.extraPrincipalReduction > Amount.ZERO) {
                        AmountText(monthSummary.mortgagePayment.extraPrincipalReduction)
                    }
                }
            }
        }
    }
}
