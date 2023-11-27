package org.hildan.accounting.ui.components

import mui.icons.material.Brightness4
import mui.icons.material.Brightness7
import mui.material.PaletteMode
import mui.material.Switch
import org.hildan.accounting.ui.global.PaletteModeContext
import react.FC
import react.create
import react.dom.aria.ariaLabel
import react.useRequiredContext

val ColorModeSwitch = FC("ColorModeSwitch") {
    var paletteMode by useRequiredContext(PaletteModeContext)

    Switch {
        icon = Brightness7.create()
        checkedIcon = Brightness4.create()
        checked = paletteMode == PaletteMode.dark
        ariaLabel = "theme"

        onChange = { _, checked ->
            paletteMode = if (checked) PaletteMode.dark else PaletteMode.light
        }
    }
}
