package org.hildan.accounting.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*

interface ColumnBuilder<T> {
    fun column(header: String, weight: Float = 1f, cellContent: @Composable BoxScope.(T) -> Unit)
}

private class Column<T>(
    val header: String,
    val weight: Float,
    val cellContent: @Composable BoxScope.(T) -> Unit,
)

private class ColumnBuilderImpl<T> : ColumnBuilder<T> {
    val columns = mutableListOf<Column<T>>()

    override fun column(header: String, weight: Float, cellContent: @Composable BoxScope.(T) -> Unit) {
        columns.add(Column(header, weight, cellContent))
    }
}

@Composable
fun <T> Table(
    items: List<T>,
    modifier: Modifier = Modifier,
    columns: ColumnBuilder<T>.() -> Unit,
) {
    val cols = remember(columns) { ColumnBuilderImpl<T>().apply { columns() }.columns }
    Column(modifier) {
        HeadersRow(cols)
        items.forEach { item ->
            ValuesRow(item, cols)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> LazyTable(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    key: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    buildColumns: ColumnBuilder<T>.() -> Unit,
) {
    val columns = remember(buildColumns) { ColumnBuilderImpl<T>().apply { buildColumns() }.columns }
    LazyColumn(modifier, state) {
        stickyHeader {
            HeadersRow(columns)
        }
        items(items = items, key = key, contentType = contentType) { item ->
            ValuesRow(item, columns)
        }
    }
}

@Composable
private fun <T> HeadersRow(cols: List<Column<T>>) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cols.forEach { column ->
            HeaderCell(column.header, column.weight)
        }
    }
}

@Composable
private fun RowScope.HeaderCell(name: String, weight: Float) {
    Box(modifier = Modifier.weight(weight)) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun <T> ValuesRow(item: T, cols: MutableList<Column<T>>) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        cols.forEach { column ->
            Box(modifier = Modifier.weight(weight = column.weight)) {
                column.cellContent(this, item)
            }
        }
    }
}
