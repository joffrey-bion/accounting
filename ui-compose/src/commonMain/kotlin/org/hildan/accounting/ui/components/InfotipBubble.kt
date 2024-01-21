package org.hildan.accounting.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun InfotipBubble(
    tooltipText: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.HelpOutline,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = remember { PlainTooltipState() }

    fun showTooltip() = scope.launch { tooltipState.show() }
    fun hideTooltip() = scope.launch { tooltipState.dismiss() }

    PlainTooltipBox(
        tooltip = { Text(tooltipText) },
        tooltipState = tooltipState,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Infotip bubble",
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // to remove the square hover highlight
                    onClick = { showTooltip() },
                    onClickLabel = "Show help message",
                )
                .onPointerEvent(PointerEventType.Enter) { showTooltip() }
                .onPointerEvent(PointerEventType.Exit) { hideTooltip() },
        )
    }
}