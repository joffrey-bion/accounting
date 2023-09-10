package org.hildan.accounting.cli

interface Table<T> {
    fun format(data: Iterable<T>): String
}

interface TableBuilder<T> {
    fun column(header: String, width: Int? = null, getValue: T.() -> Any?)
}

fun <T> table(colSeparator: String = "  ", configure: TableBuilder<T>.() -> Unit): Table<T> =
    TableImpl<T>(colSeparator).apply(configure)

private class TableImpl<T>(val colSeparator: String) : TableBuilder<T>, Table<T> {
    private val columns = mutableListOf<Column<T>>()

    override fun column(header: String, width: Int?, getValue: T.() -> Any?) {
        columns.add(Column(header, width, getValue))
    }

    override fun format(data: Iterable<T>): String {
        val colValues = data.map { element -> columns.map { it.getValue(element).toString() } }
        val colWidths = columns.mapIndexed { i, col -> col.width ?: maxOf(col.header.length, colValues.maxOf { it[i].length }) }
        val headerLine = columns.mapIndexed { i, col -> col.header.padEnd(colWidths[i]) }.joinToString(colSeparator)
        return buildString {
            appendLine(headerLine)
            append(colValues.joinToString("\n") { row ->
                row.mapIndexed { i, cell -> cell.padStart(colWidths[i]) }.joinToString(colSeparator)
            })
        }
    }
}

private data class Column<T>(
    val header: String,
    val width: Int?,
    val getValue: (T) -> Any?,
)
