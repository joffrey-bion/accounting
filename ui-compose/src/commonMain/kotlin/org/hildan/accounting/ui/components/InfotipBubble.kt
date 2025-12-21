package org.hildan.accounting.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TooltipAnchorPosition
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
    icon: ImageVector = Icons.AutoMirrored.Filled.HelpOutline,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()
    val tooltipPositionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above)

    fun showTooltip() = scope.launch { tooltipState.show() }
    fun hideTooltip() = tooltipState.dismiss()

    TooltipBox(
        positionProvider = tooltipPositionProvider,
        tooltip = {
            RichTooltip {
                Text(tooltipText)
            }
        },
        state = tooltipState,
        modifier = modifier,
        focusable = true,
        enableUserInput = true,
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
