package org.hildan.accounting.ui.components

import emotion.react.css
import mui.material.CircularProgress
import mui.material.Typography
import mui.system.Box
import react.FC
import react.PropsWithClassName
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.rem

val LoadingMessage = FC<PropsWithClassName>("LoadingMessage") { props ->
    Box {
        css(props.className) {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
        }
        CircularProgress {
            css {
                marginRight = 1.rem
            }
        }
        Typography {
            +"Loading…"
        }
    }
}
