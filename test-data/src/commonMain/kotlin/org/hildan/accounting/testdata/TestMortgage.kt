package org.hildan.accounting.testdata

import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val parkingPrice = 35_000.eur

/**
 * Options price based on our initial estimate, as recorded by Obvion and on the initial notary invoice.
 * The effective options price is 56913 EUR now, but we need to keep the old one for construction account sims.
 */
private val optionsPrice = 38_633.eur

// Based on initial options price estimate, and now recorded as such by Obvion
private val estimatedWozValue = landPrice + constructionPrice + parkingPrice + optionsPrice

private val startDate = LocalDate(year = 2023, monthNumber = 11, dayOfMonth = 17)

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

// these dates are not the dates of the bills, but the dates of the payments themselves
private val constructionBillsPayments = listOf(
    "2024-01-16" to constructionPrice * 3.pct,      // T1
    "2024-04-18" to constructionPrice * 10.pct,     // T2
    "2024-06-11" to constructionPrice * 15.pct,     // T3
    "2024-06-16" to constructionPrice * 10.pct,     // T4
    "2024-12-01" to constructionPrice * 5.pct,      // T5 TODO add real date
    "2025-01-15" to constructionPrice * "23.5".pct, // T6 TODO add real date
    "2024-11-11" to constructionPrice * 10.pct,     // T7
    "2024-11-01" to constructionPrice * "13.5".pct, // T8 TODO add real date
    "2025-09-01" to constructionPrice * 10.pct, // on delivery date TODO add real date
).map { (date, amount) ->
    // BotBouw rounds the numbers up even when less than 0.5
    Payment(date = LocalDate.parse(date), amount = amount.roundedToTheCent(RoundingMode.CEILING))
}

private val testMortgage = Mortgage(
    startDate = startDate,
    termInYears = 30,
    parts = listOf(
        MortgagePart(
            id = MortgagePartId("101"),
            amount = 350_000.eur,
            annualInterestRate = ObvionRateSept2023,
            extraPayments = listOf(
                Payment(LocalDate.parse("2024-06-07"), 50_000.eur),
                Payment(LocalDate.parse("2024-06-13"), "12202.92".eur),
            ),
        ),
        MortgagePart(
            id = MortgagePartId("102"),
            amount = 350_000.eur,
            annualInterestRate = ObvionRateSept2023,
            extraPayments = emptyList(),
        ),
    ),
    dayCountConvention = DayCountConvention.ThirtyE360ISDA,
)

val testSimulationIncremental = SimulationSettings(
    simulationName = "700k Incremental",
    mortgage = testMortgage,
    property = Property.NewConstruction(
        initialNotaryPayment = Payment(startDate, landPrice),
        constructionInstallments = listOf(
            Payment(date = LocalDate.parse("2025-03-01"), parkingPrice), // TODO add real date
            Payment(date = LocalDate.parse("2025-07-01"), optionsPrice), // TODO add real date
            *constructionBillsPayments.toTypedArray<Payment>(),
            // Compensates for bills rounding so it still amounts to the real construction price.
            // In reality, BotBouw will probably adjust the last bill, or the bank will give/take the difference.
            Payment(date = LocalDate.parse("2025-12-31"), constructionPrice - constructionBillsPayments.sumOf { it.amount }),
        ),
        wozValue = estimatedWozValue,
    ),
)

val testSimulationBulk = SimulationSettings(
    simulationName = "700k Bulk",
    mortgage = testMortgage,
    property = Property.NewConstruction(
        initialNotaryPayment = Payment(startDate, landPrice + parkingPrice + optionsPrice + constructionPrice),
        constructionInstallments = emptyList(),
        wozValue = estimatedWozValue,
    ),
)
