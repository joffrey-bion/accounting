package org.hildan.accounting.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.hildan.accounting.ui.screens.*

enum class Screen {
    IncomeTax, MortgageSimulation,
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.MortgageSimulation) }

    MaterialTheme {
        Column {
            Header(currentScreen = currentScreen, onNavigate = { currentScreen = it })
            when (currentScreen) {
                Screen.MortgageSimulation -> MortgageSimulationScreen()
                Screen.IncomeTax -> IncomeTaxScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    TopAppBar(title = { Text("Accounting") }, actions = {
        IconButton(onClick = { onNavigate(Screen.MortgageSimulation) }) {
            Icon(Icons.Default.CurrencyExchange, "Mortgage icon")
        }
        IconButton(onClick = { onNavigate(Screen.IncomeTax) }) {
            Icon(Icons.Default.AccountBalance, "Taxes icon")
        }
    })
//    NavigationBar {
//        NavigationBarItem(
//            selected = currentScreen == Screen.Mortgage,
//            icon = { Icon(Icons.Default.CurrencyExchange, "Mortgage icon") },
//            label = { Text("Mortgage") },
//            onClick = { onNavigate(Screen.Mortgage) },
//        )
//        NavigationBarItem(
//            selected = currentScreen == Screen.Taxes,
//            icon = { Icon(Icons.Default.AccountBalance, "Taxes icon") },
//            label = { Text("Taxes") },
//            onClick = { onNavigate(Screen.Taxes) },
//        )
//    }
}
