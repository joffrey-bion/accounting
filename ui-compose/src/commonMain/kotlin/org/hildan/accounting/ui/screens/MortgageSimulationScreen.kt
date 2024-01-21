package org.hildan.accounting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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

    if (simulationFormIsOpen) {
        SimulationSettingsForm(initialValue = editedSimulation, onSave = { sim ->
            val oldSim = editedSimulation
            if (oldSim == null) {
                scope.launch {
                    simulations += sim.simulateLinear()
                }
            } else {
                val index = simulations.indexOfFirst { it.settings == oldSim }
                scope.launch {
                    simulations = simulations.toMutableList().apply { set(index, sim.simulateLinear()) }
                }
            }
            closeSimulationForm()
        }, onCancel = { closeSimulationForm() })
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SimulationList(simulations = simulations, onEdit = { sim -> openSimulationForm(sim.settings) })
            IconButton(onClick = { openSimulationForm(simulation = null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add simulation")
            }
            if (simulations.isNotEmpty()) {
                MortgagePaymentsPlot(simulationResult = simulations.first())
            }
        }
    }
}
