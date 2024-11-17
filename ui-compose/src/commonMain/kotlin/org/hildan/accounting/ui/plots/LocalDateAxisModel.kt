package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

fun localDateAxisModel(
    min: LocalDate,
    max: LocalDate,
    minViewExtent: Double = (LocalDateDoubleConverter.convertFrom(max) - LocalDateDoubleConverter.convertFrom(min)) * 0.2,
    maxViewExtent: Double = LocalDateDoubleConverter.convertFrom(max) - LocalDateDoubleConverter.convertFrom(min),
    minimumMajorTickIncrement: Double = (LocalDateDoubleConverter.convertFrom(max) - LocalDateDoubleConverter.convertFrom(min)) * 0.1,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
) = linearAxisModel(
    min = min,
    max = max,
    converter = LocalDateDoubleConverter,
    minViewExtent = minViewExtent,
    maxViewExtent = maxViewExtent,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
    allowZooming = allowZooming,
    allowPanning = allowPanning,
)

private object LocalDateDoubleConverter : DoubleConverter<LocalDate> {

    override fun convertTo(value: Double): LocalDate = LocalDate.fromEpochDays(value.toInt())

    override fun convertFrom(value: LocalDate): Double = value.toEpochDays().toDouble()
}
