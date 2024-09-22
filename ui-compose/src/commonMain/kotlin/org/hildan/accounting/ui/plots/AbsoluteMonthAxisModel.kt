package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.*
import org.hildan.accounting.mortgage.*

fun absoluteMonthAxisModel(
    min: AbsoluteMonth,
    max: AbsoluteMonth,
    zoomRangeLimit: Float = (AbsoluteMonthFloatConverter.convertFrom(max) - AbsoluteMonthFloatConverter.convertFrom(min)) * 0.2f,
    minimumMajorTickIncrement: Float = (AbsoluteMonthFloatConverter.convertFrom(max) - AbsoluteMonthFloatConverter.convertFrom(min)) * 0.1f,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
) = linearAxisModel(
    min = min,
    max = max,
    converter = AbsoluteMonthFloatConverter,
    zoomRangeLimit = zoomRangeLimit,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
    allowZooming = allowZooming,
    allowPanning = allowPanning,
)

private object AbsoluteMonthFloatConverter : FloatConverter<AbsoluteMonth> {
    override fun convertTo(value: Float): AbsoluteMonth = AbsoluteMonth(
        year = (value / 12).toInt(),
        month = (value % 12).toInt(),
    )

    override fun convertFrom(value: AbsoluteMonth): Float = ((value.year - 1970) * 12 + value.month).toFloat()
}
