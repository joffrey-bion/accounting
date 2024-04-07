package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

fun localDateAxisModel(
    min: LocalDate,
    max: LocalDate,
    zoomRangeLimit: Float = (LocalDateFloatConverter.convertFrom(max) - LocalDateFloatConverter.convertFrom(min)) * 0.2f,
    minimumMajorTickIncrement: Float = (LocalDateFloatConverter.convertFrom(max) - LocalDateFloatConverter.convertFrom(min)) * 0.1f,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
) = linearAxisModel(
    min = min,
    max = max,
    converter = LocalDateFloatConverter,
    zoomRangeLimit = zoomRangeLimit,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
    allowZooming = allowZooming,
    allowPanning = allowPanning,
)

private object LocalDateFloatConverter : FloatConverter<LocalDate> {

    override fun convertTo(value: Float): LocalDate = LocalDate.fromEpochDays(value.toInt())

    override fun convertFrom(value: LocalDate): Float = value.toEpochDays().toFloat()
}
