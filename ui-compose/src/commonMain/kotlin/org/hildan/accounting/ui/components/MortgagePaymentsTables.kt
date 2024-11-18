package org.hildan.accounting.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

@Composable
fun MortgageYearlySummaryTable(yearSummaries: List<MortgageYearSummary>, modifier: Modifier = Modifier) {
    val hasConstructionAccount = yearSummaries.any { it.constructionAccount != null }
    Table(items = yearSummaries, modifier) {
        column(header = "Year") {
            Text(it.year.toString(), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Balance\nbefore") {
            AmountText(it.balanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (hasConstructionAccount) {
            column(header = "BD balance before") {
                AmountText(it.constructionAccount!!.balanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Principal reduction") {
            AmountText(it.principalReduction, modifier = Modifier.align(Alignment.CenterEnd))
        }
        column(header = "Interest rate") {
            Text(it.interestRates.joinToString(" → "), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Interest") {
            AmountText(it.interestDue, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (yearSummaries.any { it.extraPrincipalReduction > Amount.ZERO }) {
            column(header = "Extra") {
                AmountText(it.extraPrincipalReduction, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Avg. monthly payment") {
            AmountText(it.avgMonthlyPayment, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (hasConstructionAccount) {
            column(header = "BD deducted interest") {
                AmountText(it.constructionAccount!!.deductedInterest, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Total payments") {
            AmountText(it.totalPayments, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
fun MortgageMonthlySummaryTable(monthSummaries: List<MortgageMonthSummary>, modifier: Modifier = Modifier) {
    val hasConstructionAccount = monthSummaries.any { it.constructionAccount != null }
    LazyTable(items = monthSummaries, modifier = modifier, key = { it.date }) {
        column(header = "Date") {
            LocalDateText(it.date, modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Balance before") {
            AmountText(it.mortgagePayment.balanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (hasConstructionAccount) {
            column(header = "BD balance before") {
                AmountText(
                    amount = it.constructionAccount?.balanceBefore ?: Amount.ZERO,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }
        column(header = "Principal reduction") {
            AmountText(it.mortgagePayment.principalReduction, modifier = Modifier.align(Alignment.CenterEnd))
        }
        column(header = "Interest rate") {
            Text(it.mortgagePayment.averageInterestRateApplied.formatPercent(), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Interest") {
            AmountText(it.mortgagePayment.interest, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (monthSummaries.any { it.mortgagePayment.extraPrincipalReduction > Amount.ZERO }) {
            column(header = "Extra") {
                AmountText(it.mortgagePayment.extraPrincipalReduction, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        if (hasConstructionAccount) {
            column(header = "BD interest") {
                AmountText(it.constructionAccount!!.generatedInterest, modifier = Modifier.align(Alignment.CenterEnd))
            }
            column(header = "BD interest deducted") {
                AmountText(-it.constructionAccount!!.deductedInterest, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Total payment") {
            AmountText(it.totalCollected, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}
