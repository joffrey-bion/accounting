package org.hildan.accounting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.ui.components.*
import org.hildan.accounting.ui.forms.*
import org.hildan.accounting.ui.plots.*

@Composable
fun MortgageSimulationScreen() {
    val scope = rememberCoroutineScope()
    var simulations by remember { mutableStateOf(emptyList<SimulationResult>()) }

    var simulationFormIsOpen by remember { mutableStateOf(false) }
    var editedSimulation by remember { mutableStateOf<SimulationSettings?>(null) }

    fun openSimulationForm(simulation: SimulationSettings?) {
        simulationFormIsOpen = true
        editedSimulation = simulation
    }
    fun closeSimulationForm() {
        simulationFormIsOpen = false
        editedSimulation = null
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (simulationFormIsOpen) {
            SimulationSettingsForm(
                initialValue = editedSimulation,
                modifier = Modifier.align(Alignment.Center),
                onSave = { sim ->
                    val oldSim = editedSimulation
                    if (oldSim == null) {
                        scope.launch {
                            simulations += sim.simulate()
                        }
                    } else {
                        val index = simulations.indexOfFirst { it.settings == oldSim }
                        scope.launch {
                            simulations = simulations.toMutableList().apply { set(index, sim.simulate()) }
                        }
                    }
                    closeSimulationForm()
                },
                onCancel = { closeSimulationForm() },
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SimulationList(simulations = simulations, onEdit = { sim -> openSimulationForm(sim.settings) })
                IconButton(onClick = { openSimulationForm(simulation = null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add simulation")
                }
                if (simulations.isNotEmpty()) {
                    SimulationDetails(
                        simulation = simulations.first(),
                        modifier = Modifier.widthIn(max = 1200.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SimulationDetails(simulation: SimulationResult, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth().heightIn(max = 400.dp).padding(bottom = 20.dp)) {
            SimulationSummaryCard(
                simulation = simulation,
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 20.dp),
            )
            MortgagePaymentsPlot(
                simulationResult = simulation,
                modifier = Modifier.weight(2f).fillMaxHeight(),
            )
        }
        MortgageYearlySummaryTable(
            yearSummaries = simulation.yearSummaries,
            modifier = Modifier.fillMaxWidth(),
        )
        MortgageMonthlySummaryTable(
            monthSummaries = simulation.monthSummaries,
            modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp),
        )
    }
}
