package org.hildan.accounting.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.hildan.accounting.mortgage.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepaymentSchemeDropdown(
    value: RepaymentScheme,
    onValueChange: (RepaymentScheme) -> Unit,
    label: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(type = MenuAnchorType.SecondaryEditable, enabled = true),
            value = value.name,
            onValueChange = {},
            readOnly = true,
            label = label,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            RepaymentScheme.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        expanded = false
                        onValueChange(it)
                    },
                )
            }
        }
    }
}