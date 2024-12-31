package org.hildan.accounting.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.hildan.accounting.ui.screens.*

enum class Screen {
    IncomeTax, MortgageSimulation,
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.MortgageSimulation) }

    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        Column {
            Header(currentScreen = currentScreen, onNavigate = { currentScreen = it })
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(rememberScrollState()),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                when (currentScreen) {
                    Screen.MortgageSimulation -> MortgageSimulationScreen()
                    Screen.IncomeTax -> IncomeTaxScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    TopAppBar(
        title = { Text("Accounting") },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        actions = {
            IconButton(onClick = { onNavigate(Screen.MortgageSimulation) }) {
                Icon(Icons.Default.CurrencyExchange, "Mortgage icon")
            }
            IconButton(onClick = { onNavigate(Screen.IncomeTax) }) {
                Icon(Icons.Default.AccountBalance, "Taxes icon")
            }
        },
    )
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
