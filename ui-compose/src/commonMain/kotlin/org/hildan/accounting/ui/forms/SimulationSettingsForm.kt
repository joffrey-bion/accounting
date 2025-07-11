package org.hildan.accounting.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.interest.*
import kotlin.time.Clock

private val defaultStartDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

private val defaultMortgage = Mortgage(
    startDate = defaultStartDate,
    termInYears = 30,
    parts = listOf(defaultPart(index = 0)),
)

internal fun defaultPart(index: Int): MortgagePart = MortgagePart(
    id = MortgagePartId((index + 1).toString()),
    amount = 400_000.eur,
    annualInterestRate = InterestRate.DynamicLtv(
        ratesPerLtvRatio = mapOf(
            60.pct to "3.58".pct,
            70.pct to "3.62".pct,
            80.pct to "3.67".pct,
            90.pct to "3.76".pct,
            106.pct to "3.87".pct,
        ),
        maxLtvRate = "4.25".pct,
        dayCountConvention = DayCountConvention.ThirtyE360ISDA,
    ),
)

private val defaultProperty = Property.Existing(
    wozValue = 400_000.eur,
    purchase = Payment(defaultStartDate, 420_000.eur),
)

@Composable
fun SimulationSettingsForm(
    initialValue: SimulationSettings? = null,
    modifier: Modifier = Modifier,
    onSave: (SimulationSettings) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember(initialValue) { mutableStateOf(initialValue?.simulationName) }
    var mortgage by remember(initialValue) { mutableStateOf(initialValue?.mortgage ?: defaultMortgage) }
    var property by remember(initialValue) { mutableStateOf(initialValue?.property ?: defaultProperty) }

    var nameError by remember { mutableStateOf(false) }

    fun validate() {
        nameError = name.isNullOrBlank()
    }

    Column(modifier) {
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
