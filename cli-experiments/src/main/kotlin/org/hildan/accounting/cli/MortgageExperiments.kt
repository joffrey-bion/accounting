package org.hildan.accounting.cli

import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val parkingPrice = 35_000.eur
private val optionsPrice = 38_633.eur

private val startDate = AbsoluteMonth(2024, 2)
private val deliveryDate = AbsoluteMonth(2025, 6)

// From: https://www.obvion.nl/Hypotheek-rente/Actuele-hypotheekrente-Obvion?duurzaamheidskorting=Ja
// The following rates were locked in on 2023-09-11
private val ObvionRate = InterestRate.LtvAdjusted(
    mapOf(
        60.pct to "3.58".pct,
        70.pct to "3.62".pct,
        80.pct to "3.67".pct,
        90.pct to "3.76".pct,
        106.pct to "3.87".pct,
    )
)

private val constructionBillsPayments = listOf(3.pct, 10.pct, 15.pct, 10.pct, 5.pct, "23.5".pct, 10.pct, "13.5".pct)
    .mapIndexed { i, f -> Payment(startDate.plusMonths(i + 1), constructionPrice * f) } +
    Payment(deliveryDate, constructionPrice * 10.pct)

private val elzenhagen36Incremental = Property.newBuild(
    installments = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, parkingPrice),
        Payment(startDate, optionsPrice),
        *constructionBillsPayments.toTypedArray(),
    )
)

private val elzenhagen36IncrNoPark = Property.newBuild(
    installments = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, optionsPrice),
        *constructionBillsPayments.toTypedArray(),
    )
)

private val elzenhagen36BulkNoParking = Property.newBuild(
    installments = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, constructionPrice),
        Payment(startDate, optionsPrice),
    )
)

fun simulateMortgages() {
    val mortgage700k = mortgage(amount = 700_000.eur)
    val sim700kIncremental = mortgage700k.simulateLinear("700k incremental", elzenhagen36Incremental)
    val sim700kIncrNoParking = mortgage700k.simulateLinear("700k incr. no park", elzenhagen36IncrNoPark)
    val sim700kBulkNoParking = mortgage700k.simulateLinear("700k bulk no park", elzenhagen36BulkNoParking)

    println(SummaryTable.format(listOf(sim700kIncremental, sim700kIncrNoParking, sim700kBulkNoParking)))
    println()
    printTables(sim700kBulkNoParking)
}

private fun printTables(simulation: MortgageSimulation) {
    println("=== Annual simulation (${simulation.name}) ===")
    println(YearTable.format(simulation.summarizeYears()))
    println()
    println("=== Monthly simulation (${simulation.name}) ===")
    println(MonthTable.format(simulation.monthlyPayments))
}

private fun mortgage(amount: Amount) = Mortgage(
    amount = amount,
    annualInterestRate = ObvionRate,
    startMonth = startDate,
    nYears = 30,
)

private val SummaryTable = table<MortgageSimulation> {
    column("Sim name", dataAlign = Align.LEFT) { name }
    column("Total loan") { mortgage.amount.format(2) }
    column("Own funds") { ownFunds.format(2) }
    column("Total interest") { totalInterest.format(2) }
    column("Avg pay") { (monthlyPayments.sumOf { it.total } / monthlyPayments.size).format(2) }
    column("Max pay") { annuitiesDistribution.max.format(2) }
    column("99p pay") { annuitiesDistribution.p99.format(2) }
    column("95p pay") { annuitiesDistribution.p95.format(2) }
    column("90p pay") { annuitiesDistribution.p90.format(2) }
}

private val YearTable = table<MortgageYearSummary> {
    column("Date") { year }
    column("N") { nMonths }
    column("Balance") { balanceBefore.format(2) }
    column("Avg Red.") { avgMonthlyRedemption.format(2) }
    column("Avg Int.") { avgMonthlyInterest.format(2) }
    column("Avg Pay.") { avgMonthlyPayment.format(2) }
}

private val MonthTable = table<MortgagePayment> {
    column("Date") { date }
    column("Balance") { balanceBefore.format(2) }
    column("Redemption") { redemption.format(2) }
    column("Interest") { interest.format(2) }
    column("Total") { total.format(2) }
}
