package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.*
import io.github.koalaplot.core.xygraph.*

internal fun <T> linearAxisModel(
    min: T,
    max: T,
    converter: FloatConverter<T>,
    zoomRangeLimit: Float = (converter.convertFrom(max) - converter.convertFrom(min)) * 0.2f,
    minimumMajorTickIncrement: Float = (converter.convertFrom(max) - converter.convertFrom(min)) * 0.1f,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
): AxisModel<T> = AxisModelAdapter(
    delegate = LinearAxisModel(
        range = converter.convertFrom(min)..converter.convertFrom(max),
        zoomRangeLimit = zoomRangeLimit,
        minimumMajorTickIncrement = minimumMajorTickIncrement,
        minimumMajorTickSpacing = minimumMajorTickSpacing,
        minorTickCount = minorTickCount,
        allowZooming = allowZooming,
        allowPanning = allowPanning,
    ),
    converter = converter,
)

internal interface FloatConverter<T> : Converter<T, Float> {
    override fun convertTo(value: Float): T
    override fun convertFrom(value: T): Float
}

internal interface Converter<T, U> {
    fun convertTo(value: U): T
    fun convertFrom(value: T): U
}

private class AxisModelAdapter<T, B>(
    val delegate: AxisModel<B>,
    val converter: Converter<T, B>,
) : AxisModel<T> {
    override val minimumMajorTickSpacing: Dp
        get() = delegate.minimumMajorTickSpacing

    override fun computeTickValues(axisLength: Dp): TickValues<T> =
        delegate.computeTickValues(axisLength).map(converter::convertTo)

    override fun computeOffset(point: T): Float = delegate.computeOffset(converter.convertFrom(point))

    override fun pan(amount: Float) = delegate.pan(amount)

    override fun zoom(zoomFactor: Float, pivot: Float) = delegate.zoom(zoomFactor, pivot)
}

private fun <T, U> TickValues<T>.map(transform: (T) -> U): TickValues<U> = object : TickValues<U> {
    override val majorTickValues: List<U> = this@map.majorTickValues.map(transform)
    override val minorTickValues: List<U> = this@map.minorTickValues.map(transform)
}