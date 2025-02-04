package org.hildan.accounting.ui.utils

internal fun <T> List<T>.withReplacedItem(index: Int, newItem: T): List<T> =
    toMutableList().apply { set(index, newItem) }
