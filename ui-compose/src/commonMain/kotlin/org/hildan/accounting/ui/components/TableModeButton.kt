package org.hildan.accounting.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

enum class SimulationTableMode(val buttonLabel: String) {
    Monthly("Monthly"),
    Yearly("Yearly"),
}

@Composable
internal fun TableModeButton(
    currentMode: SimulationTableMode,
    onModeChange: (SimulationTableMode) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        SimulationTableMode.entries.forEachIndexed { index, mode ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = SimulationTableMode.entries.size,
                ),
                onClick = { onModeChange(mode) },
                selected = mode == currentMode,
                label = { Text(mode.buttonLabel) }
            )
        }
    }
}