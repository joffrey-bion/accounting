package org.hildan.accounting.ui.desktop

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.window.*
import org.hildan.accounting.ui.*

fun main() = application {
    Window(
        title = "Accounting",
//        undecorated = true, // to remove the OS title bar from the window
        icon = rememberVectorPainter(Icons.Default.AccountBalance),
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}