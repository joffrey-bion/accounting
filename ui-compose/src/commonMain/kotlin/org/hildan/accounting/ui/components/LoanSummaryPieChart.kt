package org.hildan.accounting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import io.github.koalaplot.core.pie.*
import io.github.koalaplot.core.util.*
import org.hildan.accounting.mortgage.*

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
internal fun LoanSummaryPieChart(simulation: SimulationResult, modifier: Modifier = Modifier) {
    val labels = remember { listOf("Principal", "Interest") }
    val amounts = remember(simulation) { listOf(simulation.mortgageAmount, simulation.totalInterest) }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    PieChart(
        values = remember(amounts) { amounts.map { it.floatValue() } },
        modifier = modifier,
        slice = {
            DefaultSlice(
                color = colors[it],
                hoverExpandFactor = 1.05f,
                gap = 4f,
            )
        },
        label = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = labels[it], fontWeight = FontWeight.Bold)
                AmountText(amount = amounts[it], fontSize = 0.8.em)
            }
        },
        holeSize = 0.5f,
        labelConnector = {},
        minPieDiameter = 50.dp,
        forceCenteredPie = true,
    )
}
