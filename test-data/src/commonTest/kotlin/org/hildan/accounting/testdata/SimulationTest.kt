package org.hildan.accounting.testdata

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.mortgage.ConstructionAccountSummary
import org.hildan.accounting.mortgage.MortgageMonthSummary
import org.hildan.accounting.mortgage.simulate
import kotlin.test.Test
import kotlin.test.assertEquals

class SimulationTest {

    @Test
    fun simulationMatchesBankStatements() {
        val simulationResult = SampleSimulation.settingsIncremental.simulate()

        SampleBankData.statements.zip(simulationResult.monthSummaries).forEach { (real, simulated) ->
            assertMatches(real, simulated)
        }
    }

    @Test
    fun simulationMatchesBankNumbers() {
        val simulationResult = SampleSimulation.settingsIncremental.simulate()

        val simulatedNovember = simulationResult.monthSummaries[0]
        val simulatedDecember = simulationResult.monthSummaries[1]
        val simulatedTotalNovAndDec = simulatedNovember.totalCollected + simulatedDecember.totalCollected
        assertEquals(SampleBankData.collectionDecember.aggregated.totalDebit, simulatedTotalNovAndDec)

        // checks the correct
        val simulatedJuly = simulationResult.monthSummaries[8]
        assertEquals(SampleBankData.julyBalanceBeforePart1, simulatedJuly.mortgagePayment.partsBreakdown[0].balanceBefore)
        assertEquals(SampleBankData.julyBalanceBeforePart2, simulatedJuly.mortgagePayment.partsBreakdown[1].balanceBefore)
    }

    private fun assertMatches(statement: MonthlyStatement, simulatedMonth: MortgageMonthSummary) {
        assertEquals(statement.period, simulatedMonth.mortgagePayment.period)
        assertEquals(
            statement.collectionNotice.aggregated,
            simulatedMonth.equivalentCollectionNotice(),
            "collection notice for ${statement.period} is incorrect"
        )
        assertEquals(
            statement.constructionAccountStatement.toExpectedConstructionAccountSummary(),
            simulatedMonth.constructionAccount,
            "constructionAccount for ${statement.period} is incorrect"
        )
        assertEquals(
            statement.constructionAccountStatement.totalDebit,
            simulatedMonth.constructionAccount?.totalReduction(),
            "total reduction of the construction account is incorrect"
        )
    }

    private fun MortgageMonthSummary.equivalentCollectionNotice(): AccountDebitDetails = AccountDebitDetails(
        part101 = mortgagePayment.partsBreakdown[0].totalDue,
        part102 = mortgagePayment.partsBreakdown[1].totalDue,
        bdInterestDeduction = constructionAccount?.deductedInterest ?: Amount.ZERO,
        totalDebit = totalCollected,
    )

    private fun ConstructionAccountStatement.toExpectedConstructionAccountSummary() = ConstructionAccountSummary(
        balanceBefore = balanceBefore,
        paidBills = paidBills,
        generatedInterest = generatedInterest,
        deductedInterest = deductedInterest,
    )

    private fun ConstructionAccountSummary.totalReduction(): Amount = deductedInterest + paidBills.sumOf { it.amount }
}