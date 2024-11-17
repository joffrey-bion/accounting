package org.hildan.accounting.ui.components

import js.core.*
import mui.icons.material.*
import mui.material.*
import mui.system.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.testdata.SampleSimulation
import org.hildan.accounting.ui.components.mortgage.*
import react.*
import web.cssom.pct

val Application = FC("Application") {

    var newSimFormOpen by useState(false)
    var allSims by useState<List<SimulationResult>>(emptyList())
    var newSimToSimulate by useState<SimulationSettings?>(null)

    useEffect(newSimToSimulate) {
        newSimToSimulate?.simulateLinear()?.also { allSims += it }
        newSimToSimulate = null
    }

    Header()

    SimulationSettingsDialog {
        dialogProps = jso {
            open = newSimFormOpen
        }
        prefilledData = SampleSimulation.settingsIncremental // TODO remove this in the future
        onCreate = {
            newSimFormOpen = false
            newSimToSimulate = it
        }
        onCancel = { newSimFormOpen = false }
    }

    if (allSims.isNotEmpty()) {
        SimulationsTable {
            simulations = allSims
        }
    }

    Tooltip {
        title = ReactNode("Add a new simulation")
        Fab {
            sx {
                top = 50.pct
                left = 50.pct
            }
            onClick = { newSimFormOpen = true }
            Add()
        }
    }

    if (allSims.isNotEmpty()) {
        AnnualTable {
            simulation = allSims.first()
        }
    }
}
