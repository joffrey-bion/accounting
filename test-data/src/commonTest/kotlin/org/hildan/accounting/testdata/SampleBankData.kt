package org.hildan.accounting.testdata

import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*

object SampleBankData {

    private val EmptyConstructionAccountStatement = ConstructionAccountStatement(
        balanceBefore = "0".eur,
        totalDebit = "0".eur,
        generatedInterest = "0".eur,
        deductedInterest = "0".eur,
        paidBills = emptyList(),
    )

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
            // the mortgage started on Nov 17th, which means November is a partial month
            period = PaymentPeriod(LocalDate(2023, 11, 17), LocalDate(2023, 12, 1)),
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
                balanceBefore = 700_000.eur - "39905.21".eur,
                totalDebit = Amount.ZERO,
                generatedInterest = "965.21".eur, // calculated by hand (700000-39905.21) * 3.76% * 14 days / 360
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
                ),
            ),
            // This is not a real BD statement because we got a single one for both November and December.
            // That said, this is the equivalent if we break down by month.
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = 661060.eur, // calculated Novemeber BD balanceBefore + interest
                totalDebit = Amount.ZERO,
                generatedInterest = "2071.32".eur, // 3036.53 - 965.21 (Known Nov+Dec - calculated Nov interest)
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
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "663131.32".eur,
                totalDebit = "20630.39".eur,
                generatedInterest = "2050.25".eur,
                deductedInterest = "3036.53".eur,
                paidBills = listOf(Payment(LocalDate.parse("2024-01-16"), "17593.86".eur, "T1: 3% construction start")),
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
                ),
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
                ),
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
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "644520.43".eur,
                totalDebit = "60665.68".eur,
                generatedInterest = "1939.87".eur,
                deductedInterest = "2019.50".eur,
                paidBills = listOf(Payment(LocalDate.parse("2024-04-18"), "58646.18".eur, "T2: 10% rough lowest floor complete")),
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
                ),
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
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "585690.24".eur,
                totalDebit = "148450.94".eur,
                generatedInterest = "1636.09".eur,
                deductedInterest = "1835.49".eur,
                // both T3 & T4 were paid in the same month, and the bank released the funds in one go
                paidBills = listOf(Payment(LocalDate.parse("2024-06-18"), "146615.45".eur, "T3 (15% rough floor in private area) + T4 (10% inner cavity leaves in private area)")),
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
                ),
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
                ),
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
                ),
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
                ),
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
            period = period(2024, 11),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "740.13".eur,
                        part102 = "904.47".eur,
                        bdInterestDeduction = "603.60".eur,
                        totalDebit = "1041.00".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "904.60".eur,
                        part102 = "1105.46".eur,
                        bdInterestDeduction = "737.73".eur,
                        totalDebit = "1272.33".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "438580.63".eur,
                totalDebit = "58225.50".eur,
                generatedInterest = "715.37".eur, // we stop earning interest after 12 months
                deductedInterest = "1341.33".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2024-11-26"),
                        amount = "56884.17".eur,
                        description = "T7: 10% screed floors in private areas (including all additional and discarded work)"
                    ),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2024, 12),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "739.03".eur,
                        part102 = "903.13".eur,
                        bdInterestDeduction = "321.92".eur,
                        totalDebit = "1320.24".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "903.27".eur,
                        part102 = "1103.83".eur,
                        bdInterestDeduction = "393.45".eur,
                        totalDebit = "1613.65".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "381070.50".eur,
                totalDebit = "138533.89".eur,
                generatedInterest = "0".eur,
                deductedInterest = "715.37".eur,
                paidBills = listOf(Payment(LocalDate.parse("2024-12-13"), "137818.52".eur, "T6: 23.5% watertight roof")),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 1),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "737.94".eur,
                        part102 = "901.79".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1639.73".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "901.93".eur,
                        part102 = "1102.19".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "2004.12".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "242536.61".eur,
                totalDebit = "29323.09".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(Payment(LocalDate.parse("2025-01-21"), "29323.09".eur, "T5: 5% exterior walls of the private part")),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 2),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "736.84".eur,
                        part102 = "900.45".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1637.29".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "900.59".eur,
                        part102 = "1100.56".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "2001.15".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "213213.52".eur,
                totalDebit = "1487.00".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2025-02-06"),
                        amount = "1487.00".eur,
                        description = "25% down payment to Aanhuis for the walk-in closet",
                    ),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 3),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "529.39".eur,
                        part102 = "793.62".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1323.01".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "794.09".eur,
                        part102 = "1190.44".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1984.53".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "211726.52".eur,
                totalDebit = "87387.44".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2025-03-11"),
                        amount = "8215.10".eur,
                        description = "25% down payment V&K for the paint, plaster, and floor",
                    ),
                    Payment(
                        date = LocalDate.parse("2025-03-21"),
                        amount = "79172.34".eur,
                        description = "T8: 13.5% stucco, plaster, and tiles",
                    ),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 4),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "528.61".eur,
                        part102 = "792.45".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1321.06".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "792.91".eur,
                        part102 = "1188.68".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1981.59".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "124339.08".eur,
                totalDebit = "0".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 5),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "527.83".eur,
                        part102 = "791.28".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1319.11".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "791.74".eur,
                        part102 = "1186.92".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1978.66".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "124339.08".eur,
                totalDebit = "1318.75".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2025-05-12"),
                        amount = "1318.75".eur,
                        description = "25% down payment to Aanhuis for the washing machine cupboard",
                    ),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 6),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "527.04".eur,
                        part102 = "790.10".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1317.14".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "790.57".eur,
                        part102 = "1185.16".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1975.73".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "123020.33".eur,
                totalDebit = "0".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 7),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "526.26".eur,
                        part102 = "788.93".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1315.19".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "789.39".eur,
                        part102 = "1183.40".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1972.79".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "123020.33".eur,
                totalDebit = "0".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 8),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "398.95".eur,
                        part102 = "708.98".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1107.93".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "709.25".eur,
                        part102 = "1260.42".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1969.67".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "123020.33".eur,
                totalDebit = "0".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = emptyList(),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 9),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "398.36".eur,
                        part102 = "707.93".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1106.29".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "708.19".eur,
                        part102 = "1258.54".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1966.73".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "123020.33".eur,
                totalDebit = "57017.45".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(LocalDate.parse("2025-09-15"), "57017.45".eur, "Notary payment for parking & storage"),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 10),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "397.76".eur,
                        part102 = "706.87".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1104.63".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "707.14".eur,
                        part102 = "1256.66".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1963.80".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "66002.88".eur,
                totalDebit = "58646.16".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2025-10-29"),
                        amount = "58646.16".eur,
                        description = "T9: 10% upon delivery (-0.02€ rounding compensation as done by BotBouw)",
                    )
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 11),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "397.17".eur,
                        part102 = "705.82".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1102.99".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "706.08".eur,
                        part102 = "1254.78".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1960.86".eur,
                    ),
                ),
            ),
            constructionAccountStatement = ConstructionAccountStatement(
                balanceBefore = "7356.72".eur,
                totalDebit = "7356.72".eur,
                generatedInterest = "0".eur,
                deductedInterest = "0".eur,
                paidBills = listOf(
                    Payment(
                        date = LocalDate.parse("2025-11-27"),
                        amount = "7356.72".eur,
                        description = "Partial Kitchen bill (rest of the construction account)",
                    ),
                ),
            ),
        ),
        MonthlyStatement(
            period = period(2025, 12),
            collectionNotice = CollectionNotice(
                accountDebits = listOf(
                    AccountDebitDetails(
                        part101 = "396.58".eur,
                        part102 = "704.76".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1101.34".eur,
                    ),
                    AccountDebitDetails(
                        part101 = "705.02".eur,
                        part102 = "1252.91".eur,
                        bdInterestDeduction = "0".eur,
                        totalDebit = "1957.93".eur,
                    ),
                ),
            ),
            constructionAccountStatement = EmptyConstructionAccountStatement,
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
