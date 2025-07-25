package org.hildan.accounting.ui.plots

import androidx.compose.ui.unit.*
import org.hildan.accounting.money.*

fun amountAxisModel(
    min: Amount = Amount.ZERO,
    max: Amount,
    minViewExtent: Double = (AmountDoubleConverter.convertFrom(max) - AmountDoubleConverter.convertFrom(min)) * 0.2,
    maxViewExtent: Double = AmountDoubleConverter.convertFrom(max) - AmountDoubleConverter.convertFrom(min),
    minimumMajorTickIncrement: Double = (AmountDoubleConverter.convertFrom(max) - AmountDoubleConverter.convertFrom(min)) * 0.1,
    minimumMajorTickSpacing: Dp = 50.dp,
    minorTickCount: Int = 4,
) = linearAxisModel(
    min = min,
    max = max,
    converter = AmountDoubleConverter,
    minViewExtent = minViewExtent,
    maxViewExtent = maxViewExtent,
    minimumMajorTickIncrement = minimumMajorTickIncrement,
    minimumMajorTickSpacing = minimumMajorTickSpacing,
    minorTickCount = minorTickCount,
)

private object AmountDoubleConverter : DoubleConverter<Amount> {
    override fun convertTo(value: Double): Amount = Amount(value.toString())
    override fun convertFrom(value: Amount): Double = value.doubleValue()
}
