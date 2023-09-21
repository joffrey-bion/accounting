package org.hildan.accounting.ui.global

import js.core.jso
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import mui.material.useMediaQuery
import react.*

val PaletteModeContext = createContext<StateInstance<PaletteMode>>()

val ThemeModule = FC<PropsWithChildren> { props ->
    val prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)")
    val state = useState(if (prefersDarkMode) PaletteMode.dark else PaletteMode.light)
    val paletteMode by state
    val theme = useMemo(paletteMode) {
        createTheme(jso {
            palette = jso { mode = paletteMode }
        })
    }

    PaletteModeContext(state) {
        ThemeProvider {
            this.theme = theme

            CssBaseline {
                enableColorScheme = true
            }
            +props.children
        }
    }
}
