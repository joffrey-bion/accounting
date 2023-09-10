package org.hildan.accounting.ui

import kotlinx.browser.*

fun main() {
    window.onload = {
        document.getElementById("root")?.append("Hello")
    }
}
