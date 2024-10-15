package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.*
import org.hildan.accounting.money.*

fun amountAxisModel(
    min: Amount = Amount.ZERO,
    max: Amount,
    zoomRangeLimit: Double = (AmountDoubleConverter.convertFrom(max) - AmountDoubleConverter.convertFrom(min)) * 0.2,
    minimumMajorTickIncrement: Double = (AmountDoubleConverter.convertFrom(max) - AmountDoubleConverter.convertFrom(min)) * 0.1,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
) = linearAxisModel(
    min = min,
    max = max,
    converter = AmountDoubleConverter,
    zoomRangeLimit = zoomRangeLimit,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
    allowZooming = allowZooming,
    allowPanning = allowPanning,
)

private object AmountDoubleConverter : DoubleConverter<Amount> {
    override fun convertTo(value: Double): Amount = Amount(value.toString())
    override fun convertFrom(value: Amount): Double = value.doubleValue()
}
