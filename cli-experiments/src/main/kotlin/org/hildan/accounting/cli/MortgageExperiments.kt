package org.hildan.accounting.cli

import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.testdata.*

fun simulateMortgages() {
    val sim700kIncremental = jhHisgenpadSimulationIncremental.simulateLinear()
    val sim700kBulk = jhHisgenpadSimulationBulk.simulateLinear()

    println(SummaryTable.format(listOf(sim700kIncremental, sim700kBulk)))
    println()
    printTables(sim700kBulk)
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
