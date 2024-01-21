package org.hildan.accounting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*

@Composable
fun SimulationList(
    simulations: List<SimulationResult>,
    modifier: Modifier = Modifier,
    onEdit: (SimulationResult) -> Unit,
) {
    // TODO handle zero-state bette
    LazyColumn(modifier = modifier) {
        itemsIndexed(simulations) { index, simulation ->
            SimulationListItem(
                simulation = simulation,
                onEdit = { onEdit(simulation) },
            )
        }
    }
}

@Composable
private fun SimulationListItem(simulation: SimulationResult, onEdit: () -> Unit) {
    ListItem(
        modifier = Modifier.width(IntrinsicSize.Min),
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(simulation.name)
                Spacer(Modifier.width(10.dp))
                AmountText(simulation.mortgageAmount)
                Spacer(Modifier.width(10.dp))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        },
    )
}
