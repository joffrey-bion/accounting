package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

private val defaultStartDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

private val defaultMortgage = Mortgage(
    amount = 400_000.eur,
    annualInterestRate = InterestRate.Fixed(4.pct),
    startDate = defaultStartDate,
    termInYears = 30,
)

private val defaultProperty = Property.Existing(
    wozValue = 400_000.eur,
    purchase = Payment(defaultStartDate, 420_000.eur),
)

@Composable
fun SimulationSettingsForm(
    initialValue: SimulationSettings? = null,
    onSave: (SimulationSettings) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(initialValue?.simulationName) }
    var mortgage by remember { mutableStateOf(initialValue?.mortgage ?: defaultMortgage) }
    var property by remember { mutableStateOf(initialValue?.property ?: defaultProperty) }

    var nameError by remember { mutableStateOf(false) }

    fun validate() {
        nameError = name.isNullOrBlank()
    }

    Column {
        OutlinedTextField(
            value = name ?: "",
            onValueChange = { name = it.trim(); validate() },
            label = { Text("Simulation name") },
            isError = nameError,
            singleLine = true,
        )
        MortgageSettingsForm(mortgage, onValueChange = { mortgage = it })
        PropertySettingsForm(property, onValueChange = { property = it })
        Row {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                enabled = name != null,
                onClick = {
                    name?.let { nonNullName ->
                        onSave(
                            SimulationSettings(
                                simulationName = nonNullName,
                                mortgage = mortgage,
                                property = property
                            )
                        )
                    }
                },
            ) {
                Text("Save")
            }
        }
    }
}
