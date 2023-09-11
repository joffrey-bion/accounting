package org.hildan.data2viz

import io.data2viz.charts.chart.*
import io.data2viz.charts.core.*

object EventSynchronizer {
    fun syncOn(vararg types: GlobalEventType): GlobalEventSynchronizer = PureGlobalEventSynchronizer(types.toSet())
    fun <K> syncOn(vararg eventTypes: DataEventType): DataEventSynchronizer<K> = PureDataEventSynchronizer(eventTypes.toSet())
    fun <K> syncOn(vararg eventTypes: EventType): DataEventSynchronizer<K> = CombinedEventSynchronizer(eventTypes.toSet())
}

interface GlobalEventSynchronizer {
    fun addChart(chart: Chart<*>)
}

interface DataEventSynchronizer<K> {
    fun addChart(chart: Chart<K>) = addChart(chart) { it }
    fun <DOMAIN> addChart(chart: Chart<DOMAIN>, computeKey: (DOMAIN) -> K)
}

sealed interface EventType {
    data object Highlight : DataEventType
    data object Selection : DataEventType
    data object Zoom : GlobalEventType
    data object Pan : GlobalEventType
}
sealed interface DataEventType : EventType
sealed interface GlobalEventType : EventType

private class CombinedEventSynchronizer<K>(eventTypes: Set<EventType>) : DataEventSynchronizer<K> {
    private val dataSynchronizer = PureDataEventSynchronizer<K>(eventTypes.filterIsInstanceTo(mutableSetOf()))
    private val globalSynchronizer = PureGlobalEventSynchronizer(eventTypes.filterIsInstanceTo(mutableSetOf()))

    override fun <DOMAIN> addChart(chart: Chart<DOMAIN>, computeKey: (DOMAIN) -> K) {
        dataSynchronizer.addChart(chart, computeKey)
        globalSynchronizer.addChart(chart)
    }
}

private class PureDataEventSynchronizer<K>(val eventTypes: Set<DataEventType>) : DataEventSynchronizer<K> {

    private val syncCharts = mutableListOf<SynchronizableChart<K, *>>()

    override fun <DOMAIN> addChart(chart: Chart<DOMAIN>, computeKey: (DOMAIN) -> K) {
        val syncChart = SynchronizableChart(chart, computeKey)
        if (EventType.Highlight in eventTypes) {
            chart.onHighlight { event ->
                syncChart.doOnOtherCharts(event.data) { highlightByKeys(it) }
            }
        }
        if (EventType.Selection in eventTypes) {
            chart.onSelect { event ->
                syncChart.doOnOtherCharts(event.data) { selectByKeys(it) }
            }
        }
        syncCharts.add(syncChart)
    }

    private fun <DOMAIN> SynchronizableChart<K, DOMAIN>.doOnOtherCharts(
        eventData: List<Datum<DOMAIN>>,
        value: SynchronizableChart<K, *>.(keys: Set<K>) -> Unit,
    ) {
        val keys = eventData.mapTo(mutableSetOf()) { computeKey(it.domain) }
        syncCharts.forEach { other ->
            if (this !== other) {
                value(other, keys)
            }
        }
    }

    private class SynchronizableChart<K, DOMAIN>(
        private val chart: Chart<DOMAIN>,
        val computeKey: (DOMAIN) -> K,
    ) {
        fun highlightByKeys(keys: Set<K>) {
            chart.highlight(findDataBatchByKeys(keys))
        }

        fun selectByKeys(keys: Set<K>) {
            chart.select(findDataBatchByKeys(keys))
        }

        private fun findDataBatchByKeys(keys: Set<K>): List<Datum<DOMAIN>> =
            chart.dataset.data.filter { computeKey(it.domain) in keys }
    }
}

private class PureGlobalEventSynchronizer(val eventTypes: Set<GlobalEventType>) : GlobalEventSynchronizer {

    private val syncCharts = mutableListOf<Chart<*>>()

    override fun addChart(chart: Chart<*>) {
        if (EventType.Zoom in eventTypes) {
            chart.onZoom { event ->
                chart.doOnOtherCharts { zoom(event.zoomAction) }
            }
        }
        if (EventType.Pan in eventTypes) {
            chart.onPan { event ->
                chart.doOnOtherCharts { pan(event.panAction) }
            }
        }
        syncCharts.add(chart)
    }

    private fun Chart<*>.doOnOtherCharts(value: Chart<*>.() -> Unit) {
        syncCharts.forEach { other ->
            if (this !== other) {
                value(other)
            }
        }
    }
}
