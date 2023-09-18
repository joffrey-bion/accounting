package org.hildan.accounting.ui

import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

private val landPrice = "165894.98".eur
private val constructionPrice = "586461.79".eur
private val purchasePrice = landPrice + constructionPrice
private val parkingPrice = 35_000.eur
private val optionsPrice = 37_000.eur
internal val totalPrice = purchasePrice + parkingPrice + optionsPrice

private val annualGroundLease = 5481.eur
private val monthlyGroundLease = annualGroundLease / 12
private val startDate = AbsoluteMonth(2024, 2)
private val deliveryDate = AbsoluteMonth(2025, 6)

// From: https://www.obvion.nl/Hypotheek-rente/Actuele-hypotheekrente-Obvion?duurzaamheidskorting=Ja
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

private val elzenhagen36 = Property.newBuild(
    installments = listOf(
        Payment(startDate, landPrice),
        Payment(startDate, parkingPrice),
        Payment(startDate, optionsPrice),
        *constructionBillsPayments.toTypedArray(),
    )
)

fun simulateMortgage(simName: String, amount: Amount) = mortgage(amount).simulateLinear(simName, elzenhagen36)

private fun mortgage(amount: Amount) = Mortgage(
    amount = amount,
    annualInterestRate = ObvionRate,
    startMonth = startDate,
    nYears = 30,
)
