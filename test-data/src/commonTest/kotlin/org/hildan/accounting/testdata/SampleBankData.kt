package org.hildan.accounting.testdata

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.eur
import org.hildan.accounting.money.pct
import org.hildan.accounting.money.sumOf
import org.hildan.accounting.mortgage.DayCountConvention
import org.hildan.accounting.mortgage.Payment
import org.hildan.accounting.mortgage.PaymentPeriod

object SampleBankData {

    // the mortgage started on Nov 17th, which means November is a partial month with this ratio
    private val novemberPeriod = PaymentPeriod(LocalDate(2023, 11, 17), LocalDate(2023, 12, 1))
    private val decemberPeriod = period(2023, 12)

    private val novemberDayCountFactor = DayCountConvention.ThirtyE360.dayCountFactor(novemberPeriod)
    private val decemberDayCountFactor = DayCountConvention.ThirtyE360.dayCountFactor(decemberPeriod)

    private val bdBalanceNov = 700_000.eur - "39905.21".eur // on the first construction account statement
    private val bdInterestNov = (bdBalanceNov * "3.76".pct * novemberDayCountFactor).roundedToTheCent()

    private val bdBalanceDec = bdBalanceNov + bdInterestNov
    private val bdInterestDec = (bdBalanceDec * "3.76".pct * decemberDayCountFactor).roundedToTheCent()

    init {
        // we don't know the interest for the individual November and December periods but the first statement gives the sum
        check(bdInterestNov + bdInterestDec == "3036.53".eur) {
            "incorrect calculations of the expected BD interest breakdown: $bdInterestNov + $bdInterestDec != 3036.53"
        }
    }

    /**
     * The real collection notice that happened in December for both the November and December periods.
     */
    val collectionDecember = CollectionNotice(
        listOf(
            AccountDebitDetails(
                part101 = "255.89".eur + "1034.44".eur, // november interest + december interest&principal
                part102 = "255.89".eur + "1034.44".eur, // november interest + december interest&principal
                bdInterestDeduction = Amount.ZERO,
                totalDebit = "2580.66".eur,
            ),
            AccountDebitDetails(
                part101 = "255.89".eur + "1034.45".eur, // november interest + december interest&principal
                part102 = "255.89".eur + "1034.45".eur, // november interest + december interest&principal
                bdInterestDeduction = Amount.ZERO,
                totalDebit = "2580.68".eur,
            ),
        )
    )

    // From the july rate update notice
    val julyBalanceBeforePart1 = "280836.76".eur
    val julyBalanceBeforePart2 = "343194.45".eur

    /**
     * The real collection notices and construction account statements to check against.
     * The first 2 months (November and December) are not real statements but are constructed from real numbers to
     * match the format and simplify tests.
     */
    val statements = listOf(
        MonthlyStatement(
            period = novemberPeriod,
            collectionNotice = CollectionNotice(
                // November was never collected on its own, so we don't have a breakdown by account.
                // The initial letter does give us the amounts due per part and total for the November period, though.
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "511.78".eur,
                        part102 = "511.78".eur,
                        bdInterestDeduction = Amount.ZERO,
                        totalDebit = "1023.56".eur,
                    ),
                ),
            ),
            // This is not a real BD statement because we got a single one for both November and December.
            // That said, this is the equivalent if we break down by month.
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = bdBalanceNov,
                totalDebit = Amount.ZERO,
                generatedInterest = bdInterestNov,
                deductedInterest = Amount.ZERO,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2023, 12),
            collectionNotice = CollectionNotice(
                // December was never collected on its own, so we don't have a breakdown by account.
                // The initial letter does give us the amounts due per part and total for the December period, though.
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "2068.89".eur,
                        part102 = "2068.89".eur,
                        bdInterestDeduction = Amount.ZERO,
                        totalDebit = "4137.78".eur,
                    ),
                )
            ),
            // This is not a real BD statement because we got a single one for both November and December.
            // That said, this is the equivalent if we break down by month.
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = bdBalanceDec,
                totalDebit = Amount.ZERO,
                generatedInterest = bdInterestDec,
                deductedInterest = Amount.ZERO,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 1),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1032.92".eur,
                        part102 = "1032.92".eur,
                        bdInterestDeduction = "1518.26".eur,
                        totalDebit = "547.58".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1032.92".eur,
                        part102 = "1032.92".eur,
                        bdInterestDeduction = "1518.27".eur,
                        totalDebit = "547.57".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "663131.32".eur,
                totalDebit = "20630.39".eur,
                generatedInterest = "2050.25".eur,
                deductedInterest = "3036.53".eur,
                paidBills = listOf(Payment(LocalDate.parse("2024-01-16"), "17593.86".eur)),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 2),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1031.40".eur,
                        part102 = "1031.40".eur,
                        bdInterestDeduction = "1025.12".eur,
                        totalDebit = "1037.68".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1031.40".eur,
                        part102 = "1031.40".eur,
                        bdInterestDeduction = "1025.13".eur,
                        totalDebit = "1037.67".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "644551.18".eur,
                totalDebit = "2050.25".eur,
                generatedInterest = "2019.59".eur,
                deductedInterest = "2050.25".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 3),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1029.87".eur,
                        part102 = "1029.87".eur,
                        bdInterestDeduction = "1009.79".eur,
                        totalDebit = "1049.95".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1029.88".eur,
                        part102 = "1029.88".eur,
                        bdInterestDeduction = "1009.80".eur,
                        totalDebit = "1049.96".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "644520.52".eur,
                totalDebit = "2019.59".eur,
                generatedInterest = "2019.50".eur,
                deductedInterest = "2019.59".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 4),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1028.35".eur,
                        part102 = "1028.35".eur,
                        bdInterestDeduction = "1009.75".eur,
                        totalDebit = "1046.95".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1028.35".eur,
                        part102 = "1028.35".eur,
                        bdInterestDeduction = "1009.75".eur,
                        totalDebit = "1046.95".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "644520.43".eur,
                totalDebit = "60665.68".eur,
                generatedInterest = "1939.87".eur,
                deductedInterest = "2019.50".eur,
                paidBills = listOf(Payment(LocalDate.parse("2024-04-18"), "58646.18".eur)),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 5),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1026.83".eur,
                        part102 = "1026.83".eur,
                        bdInterestDeduction = "969.93".eur,
                        totalDebit = "1083.73".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1026.83".eur,
                        part102 = "1026.83".eur,
                        bdInterestDeduction = "969.94".eur,
                        totalDebit = "1083.72".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "585794.62".eur,
                totalDebit = "1939.87".eur,
                generatedInterest = "1835.49".eur,
                deductedInterest = "1939.87".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 6),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "1025.30".eur,
                        part102 = "1025.30".eur,
                        bdInterestDeduction = "917.74".eur,
                        totalDebit = "1132.87".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "1025.31".eur,
                        part102 = "1025.31".eur,
                        bdInterestDeduction = "917.75".eur,
                        totalDebit = "1132.86".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "585690.24".eur,
                totalDebit = "148450.94".eur,
                generatedInterest = "1636.09".eur,
                deductedInterest = "1835.49".eur,
                // both T3 & T4 were paid in the same month, and the bank released the funds in one go
                paidBills = listOf(Payment(LocalDate.parse("2024-06-18"), "146615.45".eur)),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 7),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "744.51".eur,
                        part102 = "909.82".eur,
                        bdInterestDeduction = "736.24".eur,
                        totalDebit = "918.09".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "909.95".eur,
                        part102 = "1112.01".eur,
                        bdInterestDeduction = "899.85".eur,
                        totalDebit = "1122.11".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "438875.39".eur,
                totalDebit = "1636.09".eur,
                generatedInterest = "1342.23".eur,
                deductedInterest = "1636.09".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 8),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "743.41".eur,
                        part102 = "908.48".eur,
                        bdInterestDeduction = "604.00".eur,
                        totalDebit = "1047.89".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "908.62".eur,
                        part102 = "1110.37".eur,
                        bdInterestDeduction = "738.23".eur,
                        totalDebit = "1280.76".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "438581.53".eur,
                totalDebit = "1342.23".eur,
                generatedInterest = "1341.33".eur,
                deductedInterest = "1342.23".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 9),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "742.32".eur,
                        part102 = "907.15".eur,
                        bdInterestDeduction = "603.60".eur,
                        totalDebit = "1045.87".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "907.28".eur,
                        part102 = "1108.73".eur,
                        bdInterestDeduction = "737.73".eur,
                        totalDebit = "1278.28".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "438580.63".eur,
                totalDebit = "1341.33".eur,
                generatedInterest = "1341.33".eur,
                deductedInterest = "1341.33".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 10),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "741.22".eur,
                        part102 = "905.81".eur,
                        bdInterestDeduction = "603.60".eur,
                        totalDebit = "1043.43".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "905.94".eur,
                        part102 = "1107.10".eur,
                        bdInterestDeduction = "737.73".eur,
                        totalDebit = "1275.31".eur,
                    ),
                )
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "438580.63".eur,
                totalDebit = "1341.33".eur,
                generatedInterest = "1341.33".eur,
                deductedInterest = "1341.33".eur,
                paidBills = emptyList(),
            ),
        ),
    )
}

private fun period(year: Int, month: Int): PaymentPeriod {
    val start = LocalDate(year, month, 1)
    return PaymentPeriod(start, start.plus(1, DateTimeUnit.MONTH))
}

data class MonthlyStatement(
    val period: PaymentPeriod,
    val constructionAccountStatement: ConstructionAccountStatement,
    val collectionNotice: CollectionNotice,
)

/**
 * Construction account (Bouwdepot) statement.
 */
data class ConstructionAccountStatement(
    val balanceBefore: Amount,
    val totalDebit: Amount,
    // second page
    val generatedInterest: Amount,
    val deductedInterest: Amount, // used to reduce the monthly payment
    val paidBills: List<Payment>,
)

/**
 * Collection notice indicating how much will be debited from multiple accounts for some period of time (usually a month).
 */
data class CollectionNotice(
    val accountDebits: List<AccountDebitDetails>,
) {
    val aggregated = AccountDebitDetails(
        part101 = accountDebits.sumOf { it.part101 },
        part102 = accountDebits.sumOf { it.part102 },
        bdInterestDeduction = accountDebits.sumOf { it.bdInterestDeduction },
        totalDebit = accountDebits.sumOf { it.totalDebit },
    )
}

/**
 * Part of a collection notice indicating how much will be debited from a single account for the relevant period.
 */
data class AccountDebitDetails(
    val part101: Amount,
    val part102: Amount,
    val bdInterestDeduction: Amount,
    val totalDebit: Amount,
)
