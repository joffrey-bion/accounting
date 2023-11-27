package org.hildan.accounting.ui.components

import mui.icons.material.AccountBalance
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import web.cssom.number
import web.cssom.rem

val Header = FC("Header") {
    AppBar {
        position = AppBarPosition.sticky

        Toolbar {
            AccountBalance {
                sx { marginRight = 1.rem }
            }
            Typography {
                sx { flexGrow = number(1.0) }
                variant = TypographyVariant.h6
                noWrap = true

                +"Accounting"
            }

            ColorModeSwitch()
        }
    }
}
