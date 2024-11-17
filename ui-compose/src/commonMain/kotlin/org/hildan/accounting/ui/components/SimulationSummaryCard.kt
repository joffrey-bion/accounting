package org.hildan.accounting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*

@Composable
internal fun SimulationSummaryCard(simulation: SimulationResult, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(all = 15.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            LoanSummaryPieChart(
                simulation = simulation,
                modifier = Modifier.padding(all = 10.dp).fillMaxWidth().heightIn(max = 150.dp),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total payments: ", fontWeight = FontWeight.Bold)
                AmountText(simulation.totalPayments)
            }
            Column {
                Text(text = "Monthly payments: ", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                ) {
                    VerticalDivider(modifier = Modifier.padding(start = 30.dp, end = 15.dp).fillMaxHeight())
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        Text("Max")
                        Text("P99")
                        Text("P95")
                        Text("P90")
                        Text("Average")
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        AmountText(simulation.annuitiesDistribution.max)
                        AmountText(simulation.annuitiesDistribution.p99)
                        AmountText(simulation.annuitiesDistribution.p95)
                        AmountText(simulation.annuitiesDistribution.p90)
                        AmountText(simulation.annuitiesDistribution.average)
                    }
                }
            }
        }
    }
}
