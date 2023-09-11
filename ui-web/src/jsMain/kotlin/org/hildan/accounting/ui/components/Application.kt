package org.hildan.accounting.ui.components

import mui.material.*
import react.*

val Application = FC("Application") {
    Card {
        CardHeader {
            title = ReactNode("Balance")
        }
        CardContent {
            +"Hello, your balance is 42€"
        }
    }
}
