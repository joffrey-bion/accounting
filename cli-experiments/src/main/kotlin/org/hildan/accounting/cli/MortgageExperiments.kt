package org.hildan.accounting.cli

import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property.*

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val parkingPrice = 35_000.eur
private val optionsPrice = 38_633.eur

private val startDate = AbsoluteMonth(2024, 2)
private val deliveryDate = AbsoluteMonth(2025, 6)

// From: https://www.obvion.nl/Hypotheek-rente/Actuele-hypotheekrente-Obvion?duurzaamheidskorting=Ja
// The following rates were locked in on 2023-09-11
private val ObvionRate = InterestRate.DynamicLtv(
    mapOf(
        60.pct to "3.58".pct,
        70.pct to "3.62".pct,
        80.pct to "3.67".pct,
        90.pct to "3.76".pct,
        106.pct to "3.87".pct,
    )
)

private val mortgage700k = Mortgage(
    amount = 700_000.eur,
    annualInterestRate = ObvionRate,
    startMonth = startDate,
    termInYears = 30,
)

private val constructionBillsPayments = listOf(3.pct, 10.pct, 15.pct, 10.pct, 5.pct, "23.5".pct, 10.pct, "13.5".pct)
    .mapIndexed { i, f -> Payment(startDate.plusMonths(i + 1), constructionPrice * f) } +
    Payment(deliveryDate, constructionPrice * 10.pct)

private val elzenhagen36Incremental = run {
    val installments1 = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, parkingPrice),
        Payment(startDate, optionsPrice),
        *constructionBillsPayments.toTypedArray(),
    )
    NewConstruction(installments = installments1)
}

private val elzenhagen36IncrNoPark = run {
    val installments1 = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, optionsPrice),
        *constructionBillsPayments.toTypedArray(),
    )
    NewConstruction(installments = installments1)
}

private val elzenhagen36BulkNoParking = run {
    val installments1 = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, constructionPrice),
        Payment(startDate, optionsPrice),
    )
    NewConstruction(installments = installments1)
}

fun simulateMortgages() {
    val sim700kIncremental =
        SimulationSettings(simulationName = "700k incremental", mortgage700k, elzenhagen36Incremental).simulateLinear()
    val sim700kIncrNoParking =
        SimulationSettings(simulationName = "700k incr. no park", mortgage700k, elzenhagen36IncrNoPark).simulateLinear()
    val sim700kBulkNoParking =
        SimulationSettings(simulationName = "700k bulk no park", mortgage700k, elzenhagen36BulkNoParking).simulateLinear()

    println(SummaryTable.format(listOf(sim700kIncremental, sim700kIncrNoParking, sim700kBulkNoParking)))
    println()
    printTables(sim700kBulkNoParking)
}

private fun printTables(simulation: SimulationResult) {
    println("=== Annual simulation (${simulation.name}) ===")
    println(YearTable.format(simulation.summarizeYears()))
    println()
    println("=== Monthly simulation (${simulation.name}) ===")
    println(MonthTable.format(simulation.monthlyPayments))
}

private val SummaryTable = table<SimulationResult> {
    column("Sim name", dataAlign = Align.LEFT) { name }
    column("Total loan") { mortgageAmount.format(2) }
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
    column("Avg P.Red.") { avgMonthlyPrincipalReduction.format(2) }
    column("Avg Int.") { avgMonthlyInterest.format(2) }
    column("Avg Pay.") { avgMonthlyPayment.format(2) }
}

private val MonthTable = table<MortgagePayment> {
    column("Date") { date }
    column("Balance") { balanceBefore.format(2) }
    column("P. Reduction") { principalReduction.format(2) }
    column("Interest") { interest.format(2) }
    column("Total") { total.format(2) }
}
