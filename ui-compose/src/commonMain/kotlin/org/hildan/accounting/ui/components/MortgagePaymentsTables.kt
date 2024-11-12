package org.hildan.accounting.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

@Composable
fun MortgageYearSummaryTable(yearlySummaries: List<MortgageYearSummary>, modifier: Modifier = Modifier) {
    Table(items = yearlySummaries, modifier) {
        column(header = "Year") {
            Text(it.year.toString(), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Balance\nbefore") {
            AmountText(it.balanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (yearlySummaries.any { it.constructionAccountBalanceBefore > Amount.ZERO }) {
            column(header = "Const. acc. balance before") {
                AmountText(it.constructionAccountBalanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Principal reduction") {
            AmountText(it.principalReduction, modifier = Modifier.align(Alignment.CenterEnd))
        }
        column(header = "Interest rate") {
            Text(it.interestRates.joinToString(" → "), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Interest") {
            AmountText(it.interest, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (yearlySummaries.any { it.extraPrincipalReduction > Amount.ZERO }) {
            column(header = "Extra") {
                AmountText(it.extraPrincipalReduction, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Avg. monthly payment") {
            AmountText(it.avgMonthlyPayment, modifier = Modifier.align(Alignment.CenterEnd))
        }
        column(header = "Total payments") {
            AmountText(it.totalPayments, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
fun MortgagePaymentsTable(monthlyPayments: List<MortgagePayment>, modifier: Modifier = Modifier) {
    LazyTable(items = monthlyPayments, modifier = modifier, key = { it.date }) {
        column(header = "Date") {
            LocalDateText(it.date, modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Balance before") {
            AmountText(it.balanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (monthlyPayments.any { it.constructionAccountBalanceBefore > Amount.ZERO }) {
            column(header = "BD balance before") {
                AmountText(it.constructionAccountBalanceBefore, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Principal reduction") {
            AmountText(it.principalReduction, modifier = Modifier.align(Alignment.CenterEnd))
        }
        column(header = "Interest rate") {
            Text(it.appliedInterestRate.formatPercent(), modifier = Modifier.align(Alignment.Center))
        }
        column(header = "Interest") {
            AmountText(it.interest, modifier = Modifier.align(Alignment.CenterEnd))
        }
        if (monthlyPayments.any { it.extraPrincipalReduction > Amount.ZERO }) {
            column(header = "Extra") {
                AmountText(it.extraPrincipalReduction, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        if (monthlyPayments.any { it.constructionAccountGeneratedInterest > Amount.ZERO }) {
            column(header = "BD interest") {
                AmountText(it.constructionAccountGeneratedInterest, modifier = Modifier.align(Alignment.CenterEnd))
            }
            column(header = "BD interest deducted") {
                AmountText(-it.constructionAccountDeductedInterest, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        column(header = "Total payment") {
            AmountText(it.total, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}
