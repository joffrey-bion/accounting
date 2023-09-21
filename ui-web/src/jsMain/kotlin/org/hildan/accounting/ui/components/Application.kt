package org.hildan.accounting.ui.components

import mui.material.Card
import mui.material.CardContent
import mui.material.CardHeader
import react.FC
import react.ReactNode

val Application = FC("Application") {
    Header()

    // some long content to test scroll
    repeat(20) {
        Card {
            CardHeader {
                title = ReactNode("Balance")
            }
            CardContent {
                +"Hello, your balance is 42€"
            }
        }
    }
}
