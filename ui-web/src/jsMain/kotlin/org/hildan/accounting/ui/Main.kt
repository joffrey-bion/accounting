package org.hildan.accounting.ui

import org.hildan.accounting.ui.components.*
import react.*
import react.dom.client.*
import web.dom.*
import web.html.*
import web.window.*

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
        Application()
    }
    createRoot(rootElement).render(strictApp)
}
