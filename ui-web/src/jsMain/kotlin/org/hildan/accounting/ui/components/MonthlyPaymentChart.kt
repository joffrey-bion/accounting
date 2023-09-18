package org.hildan.accounting.ui.components

import io.data2viz.charts.chart.*
import io.data2viz.charts.chart.mark.*
import io.data2viz.charts.core.*
import io.data2viz.geom.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.data2viz.*
import react.*

external interface MonthlyPaymentsChartProps : Props {
    var size: Size
    var simulation: MortgageSimulation
}

val MonthlyPaymentsChart = FC<MonthlyPaymentsChartProps> { props ->
    vizContainer {
        size = props.size
        chart(data = props.simulation.monthlyPayments.seriesBreakdown()) {
            title = "Monthly payments (${props.simulation.name})"

            config {
                events {
                    zoomMode = ZoomMode.X
                    selectionMode = SelectionMode.Disabled
                }
            }

            val time = discrete({ domain.month }) { name = "Time" }
            val amount = quantitative({ domain.amount.doubleValue() }) {
                name = "Amount"
                formatter = { "${formatToDecimal()} €"}
            }

            series = discrete({ domain.type })

            area(time, amount) {
                stacking = Stacking.Standard
            }
        }
    }
}

private data class PaymentBreakdownItem(
    val month: AbsoluteMonth,
    val type: PaymentBreakdownItemType,
    val amount: Amount,
)

private fun List<MortgagePayment>.seriesBreakdown() = flatMap { it.breakdown() }

private fun MortgagePayment.breakdown(): List<PaymentBreakdownItem> = listOf(
    PaymentBreakdownItem(month = date, PaymentBreakdownItemType.Redemption, redemption),
    PaymentBreakdownItem(month = date, PaymentBreakdownItemType.Interest, interest),
)

private enum class PaymentBreakdownItemType {
    Redemption,
    Interest,
}
