package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.*
import org.hildan.accounting.money.*

fun amountAxisModel(
    min: Amount = Amount.ZERO,
    max: Amount,
    zoomRangeLimit: Float = (AmountFloatConverter.convertFrom(max) - AmountFloatConverter.convertFrom(min)) * 0.2f,
    minimumMajorTickIncrement: Float = (AmountFloatConverter.convertFrom(max) - AmountFloatConverter.convertFrom(min)) * 0.1f,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
    allowZooming: Boolean = true,
    allowPanning: Boolean = true,
) = linearAxisModel(
    min = min,
    max = max,
    converter = AmountFloatConverter,
    zoomRangeLimit = zoomRangeLimit,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
    allowZooming = allowZooming,
    allowPanning = allowPanning,
)

private object AmountFloatConverter : FloatConverter<Amount> {
    override fun convertTo(value: Float): Amount = Amount(value.toString())
    override fun convertFrom(value: Amount): Float = value.doubleValue().toFloat()
}
