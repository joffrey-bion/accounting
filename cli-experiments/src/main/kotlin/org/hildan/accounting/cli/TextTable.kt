package org.hildan.accounting.cli

interface Table<T> {
    fun format(data: Iterable<T>): String
}

interface TableBuilder<T> {
    fun column(
        header: String,
        dataAlign: Align = Align.RIGHT,
        headerAlign: Align = Align.LEFT,
        width: Int? = null,
        getValue: T.() -> Any?,
    )
}

enum class Align {
    LEFT,
    CENTER,
    RIGHT;
}

fun <T> table(colSeparator: String = "  ", configure: TableBuilder<T>.() -> Unit): Table<T> =
    TableImpl<T>(colSeparator).apply(configure)

private class TableImpl<T>(val colSeparator: String) : TableBuilder<T>, Table<T> {
    private val columns = mutableListOf<Column<T>>()

    override fun column(header: String, dataAlign: Align, headerAlign: Align, width: Int?, getValue: T.() -> Any?) {
        columns.add(Column(header, headerAlign, dataAlign, width, getValue))
    }

    override fun format(data: Iterable<T>): String {
        val colValues = data.map { element -> columns.map { it.getValue(element).toString() } }
        val colWidths = columns.mapIndexed { i, col -> col.width ?: maxOf(col.header.length, colValues.maxOf { it[i].length }) }
        val headerLine = columns.mapIndexed { i, col -> col.paddedHeader(colWidths[i]) }.joinToString(colSeparator)
        return buildString {
            appendLine(headerLine)
            append(colValues.joinToString("\n") { row ->
                row.mapIndexed { i, cell -> cell.pad(columns[i].dataAlign, colWidths[i]) }.joinToString(colSeparator)
            })
        }
    }
}

private fun String.pad(align: Align, width: Int) = when(align) {
    Align.LEFT -> padEnd(width)
    Align.RIGHT -> padStart(width)
    Align.CENTER -> padStart(width - (width - length) / 2).padEnd(width)
}

private data class Column<T>(
    val header: String,
    val headerAlign: Align,
    val dataAlign: Align,
    val width: Int?,
    val getValue: (T) -> Any?,
) {
    fun paddedHeader(width: Int) = header.pad(headerAlign, width)
}
