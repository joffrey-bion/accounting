package org.hildan.accounting.ui.components

import mui.material.*
import mui.material.Size
import mui.system.*
import mui.system.Box
import react.*
import web.cssom.*

external interface LoadableFabProps : Props {
    var isLoading: Boolean?
    var fabProps: FabProps?
    var progressProps: CircularProgressProps?
}

val LoadableFab = FC<LoadableFabProps> { props ->
    Box {
        sx {
            position = Position.relative
        }
        Fab {
            sx {
                position = Position.absolute
                top = 50.pct
                left = 50.pct
                transform = translate((-50).pct, (-50).pct)
            }
            size = Size.small
            +props.fabProps
        }
        if (props.isLoading == true) {
            Box {
                sx {
                    position = Position.absolute
                    top = 50.pct
                    left = 50.pct
                    transform = translate((-50).pct, (-50).pct)
                }
                CircularProgress {
                    sx {
                        // to avoid line-height issues due to it being an inline <span>
                        display = Display.block
                    }
                    size = 56
                    +props.progressProps
                }
            }
        }
    }
}