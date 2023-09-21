package org.hildan.accounting.ui

import org.hildan.accounting.ui.components.Application
import org.hildan.accounting.ui.global.ThemeModule
import react.StrictMode
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.html.HTMLElement
import web.window.window

fun main() {
    window.onload = {
        init()
    }
}

private fun init() {
    val rootElement = document.getElementById("root")
    if (rootElement == null) {
        console.error("Element with ID 'root' was not found, cannot bootstrap react app")
        return
    }
    renderRoot(rootElement)
}

private fun renderRoot(rootElement: HTMLElement) {
    val strictApp = StrictMode.create {
        ThemeModule {
            Application()
        }
    }
    createRoot(rootElement).render(strictApp)
}
