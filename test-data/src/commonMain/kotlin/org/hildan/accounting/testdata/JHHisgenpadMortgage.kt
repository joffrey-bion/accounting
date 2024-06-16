package org.hildan.accounting.testdata

import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val parkingPrice = 35_000.eur
private val optionsPrice = 56_913.eur

// Based on initial options price estimate, and now recorded as such by Obvion
private val estimatedWozValue = landPrice + constructionPrice + parkingPrice + 38_633.eur

private val startDate = LocalDate(year = 2024, monthNumber = 11, dayOfMonth = 17)

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

private val constructionBillsPayments = listOf(
    Payment(date = LocalDate.parse("2024-01-16"), amount = constructionPrice * 3.pct),
    Payment(date = LocalDate.parse("2024-04-18"), amount = constructionPrice * 10.pct),
    Payment(date = LocalDate.parse("2024-06-11"), amount = constructionPrice * 15.pct),
    Payment(date = LocalDate.parse("2024-06-16"), amount = constructionPrice * 10.pct),
    Payment(date = LocalDate.parse("2024-08-01"), amount = constructionPrice * 5.pct),      // TODO add real date
    Payment(date = LocalDate.parse("2024-09-01"), amount = constructionPrice * "23.5".pct), // TODO add real date
    Payment(date = LocalDate.parse("2024-10-01"), amount = constructionPrice * 10.pct),     // TODO add real date
    Payment(date = LocalDate.parse("2024-11-01"), amount = constructionPrice * "13.5".pct), // TODO add real date
    Payment(date = LocalDate.parse("2025-07-01"), amount = constructionPrice * 10.pct), // on delivery date
)

val jhHisgenpadSimulationIncremental = SimulationSettings(
    simulationName = "700k Incremental",
    mortgage = Mortgage(
        amount = 700_000.eur,
        annualInterestRate = ObvionRateSept2023,
        startDate = startDate,
        termInYears = 30,
    ),
    property = Property.NewConstruction(
        installments = listOf(
            Payment(startDate, landPrice),
            Payment(startDate, parkingPrice),
            Payment(startDate, optionsPrice),
            *constructionBillsPayments.toTypedArray<Payment>(),
        ),
        wozValue = estimatedWozValue,
    ),
)

val jhHisgenpadSimulationBulk = SimulationSettings(
    simulationName = "700k Bulk",
    mortgage = Mortgage(
        amount = 700_000.eur,
        annualInterestRate = ObvionRateSept2023,
        startDate = startDate,
        termInYears = 30,
    ),
    property = Property.NewConstruction(
        installments = listOf(
            Payment(startDate, landPrice),
            Payment(startDate, parkingPrice),
            Payment(startDate, optionsPrice),
            Payment(startDate, constructionPrice),
        ),
        wozValue = estimatedWozValue,
    ),
)
