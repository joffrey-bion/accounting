package org.hildan.accounting.testdata

import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val parkingPrice = 35_000.eur
private val optionsPrice = 38_633.eur

private val startDate = AbsoluteMonth(2024, 2)
private val deliveryDate = AbsoluteMonth(2025, 6)

// From: https://www.obvion.nl/Hypotheek-rente/Actuele-hypotheekrente-Obvion?duurzaamheidskorting=Ja
// The following rates were locked in on 2023-09-11
private val ObvionRateSept2023 = InterestRate.DynamicLtv(
    mapOf(
        60.pct to "3.58".pct,
        70.pct to "3.62".pct,
        80.pct to "3.67".pct,
        90.pct to "3.76".pct,
        106.pct to "3.87".pct,
    )
)

private val deliveryPayment = Payment(date = deliveryDate, amount = constructionPrice * 10.pct)
private val constructionBillsPayments = listOf(3.pct, 10.pct, 15.pct, 10.pct, 5.pct, "23.5".pct, 10.pct, "13.5".pct)
        .mapIndexed { i, f -> Payment(startDate.plusMonths(i + 1), constructionPrice * f) } + deliveryPayment

val jhHisgenpadSimulationIncremental = SimulationSettings(
    simulationName = "700k Incr. No park",
    mortgage = Mortgage(
        amount = 700_000.eur,
        annualInterestRate = ObvionRateSept2023,
        startMonth = startDate,
        termInYears = 30,
    ),
    property = Property.NewConstruction(
        installments = listOf(
            Payment(startDate, landPrice),
            Payment(startDate, parkingPrice),
            Payment(startDate, optionsPrice),
            *constructionBillsPayments.toTypedArray<Payment>(),
        )
    ),
)

val jhHisgenpadSimulationBulk = SimulationSettings(
    simulationName = "700k Bulk No park",
    mortgage = Mortgage(
        amount = 700_000.eur,
        annualInterestRate = ObvionRateSept2023,
        startMonth = startDate,
        termInYears = 30,
    ),
    property = Property.NewConstruction(
        installments = listOf(
            Payment(startDate, landPrice),
            Payment(startDate, parkingPrice),
            Payment(startDate, optionsPrice),
            Payment(startDate, constructionPrice),
        )
    ),
)