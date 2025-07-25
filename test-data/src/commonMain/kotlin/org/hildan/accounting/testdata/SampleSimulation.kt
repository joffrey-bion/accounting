package org.hildan.accounting.testdata

import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property
import org.hildan.accounting.mortgage.interest.*

object SampleSimulation {
    /**
     * The land purchase price as specified in the purchase contract (Koopovereenkomst, or KO).
     * It includes development costs (106071.90), pre-payment for the canon (11667.77), and VAT (48155.31).
     */
    private val landPrice = "165894.98".eur

    /**
     * The total construction price as specified in the construction contract (Aannemingsovereenkomst, or AO).
     * It is paid in installments during the construction.
     */
    private val constructionPrice = "586461.79".eur

    /**
     * The price of the parking spot, paid later.
     */
    private val parkingPrice = 35_000.eur

    /**
     * Options price based on our initial estimate, as written in the initial notary invoice.
     */
    private val optionsPrice = 38_633.eur

    /**
     * The estimated value of the property, based on our initial options price estimate, and now recorded as such
     * by Obvion.
     */
    private val estimatedWozValue = landPrice + constructionPrice + parkingPrice + optionsPrice

    /**
     * The initial balance of the construction account.
     * This number is not visible per se, but can be calculated from the initial completion statement at the notary,
     * where these 3 values were extracted from the mortgage balance to put them on the construction account.
     */
    val initialConstructionAccountBalance = constructionPrice + parkingPrice + optionsPrice

    /**
     * The date of the signature of the initial purchase, which marks the start of the mortgage.
     */
    private val closingDate = LocalDate(year = 2023, month = 11, day = 17)

    // From: https://www.obvion.nl/Hypotheek-rente/Actuele-hypotheekrente-Obvion?duurzaamheidskorting=Ja
    // The following rates were locked in on 2023-09-11
    private val ObvionRateSept2023 = InterestRate.DynamicLtv(
        ratesPerLtvRatio = mapOf(
            60.pct to "3.58".pct,
            70.pct to "3.62".pct,
            80.pct to "3.67".pct,
            90.pct to "3.76".pct,
            106.pct to "3.87".pct,
        ),
        maxLtvRate = "4.25".pct,
        dayCountConvention = DayCountConvention.ThirtyE360ISDA,
    )

    // These dates are not the dates of the bills, but the dates at which the bank released the funds and paid.
    private val constructionBillsPayments = listOf(
        billPayment(
            date = "2024-01-16",
            amount = constructionPrice * 3.pct,
            // Not sure why BotBouw rounded like this: 17593.8537 -> 17593.86
            roundingMode = RoundingMode.CEILING,
            description = "T1: 3% construction start",
        ),
        billPayment(
            date = "2024-04-18",
            amount = constructionPrice * 10.pct, // 58646.179 -> 58646.18
            description = "T2: 10% rough lowest floor complete",
        ),
        // Note: both T3 and T4 were paid in the same month, and the bank released the funds in one go on June 18th.
        billPayment(
            date = "2024-06-18",
            amount = constructionPrice * (15.pct + 10.pct), // 146615.4475 -> 146615.45
            description = "T3 (15% rough floor in private area) + T4 (10% inner cavity leaves in private area)",
        ),
        billPayment(
            date = "2024-11-26",
            amount = ((constructionPrice * 10.pct).roundedToTheCent() + -("1456.21".eur) * (21.pct + 1)), // 56884.1649 -> 56884.17
            description = "T7: 10% screed floors in private areas (excluding discarded work KMW36 with 21%VAT)",
        ),
        billPayment(
            date = "2024-12-13",
            amount = constructionPrice * "23.5".pct, // 137818.52065 -> 137818.52
            description = "T6: 23.5% watertight roof",
        ),
        billPayment(
            date = "2025-01-21",
            amount = constructionPrice * 5.pct, // 29323.0895 -> 29323.09
            description = "T5: 5% exterior walls of the private part",
        ),
        billPayment(
            date = "2025-03-21",
            amount = constructionPrice * "13.5".pct, // 79172.34165 -> 79172.34
            description = "T8: 13.5% stucco, plaster, and tiles",
        ),
        billPayment(
            date = "2025-10-01", // TODO change to real date
            amount = constructionPrice * 10.pct,
            description = "on delivery date",
        ),
    )

    // These dates are not the dates of the bills, but the dates at which the bank released the funds and paid.
    private val otherBillsPayments = listOf(
        Payment(
            date = LocalDate.parse("2025-02-06"),
            amount = "1487.00".eur,
            description = "25% down payment to Aanhuis for the walk-in closet",
        ),
        Payment(
            date = LocalDate.parse("2025-03-11"),
            amount = "8215.10".eur,
            description = "25% down payment V&K for the paint, plaster, and floor",
        ),
        Payment(
            date = LocalDate.parse("2025-05-12"),
            amount = "1318.75".eur,
            description = "25% down payment to Aanhuis for the washing machine cupboard",
        ),
    )

    private fun billPayment(
        date: String,
        amount: Amount,
        roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
        description: String,
    ): Payment = Payment(
        date = LocalDate.parse(date),
        amount = amount.roundedToTheCent(roundingMode),
        description = description,
    )

    private val mortgage = Mortgage(
        startDate = closingDate,
        termInYears = 30,
        parts = listOf(
            MortgagePart(
                id = MortgagePartId("101"),
                amount = 350_000.eur,
                annualInterestRate = ObvionRateSept2023,
                repaymentScheme = RepaymentScheme.Linear,
                extraPayments = listOf(
                    Payment(LocalDate.parse("2024-06-06"), 50_000.eur, "Extra payment #1"),
                    Payment(LocalDate.parse("2024-06-12"), "12202.92".eur, "Extra payment #1 (part 2, to make a whole percentage)"),
                    Payment(LocalDate.parse("2025-02-26"), "50705.08".eur, "Extra payment #2"),
                    Payment(LocalDate.parse("2025-07-21"), "34456.56".eur, "Extra payment #3"),
                ),
            ),
            MortgagePart(
                id = MortgagePartId("102"),
                amount = 350_000.eur,
                annualInterestRate = ObvionRateSept2023,
                repaymentScheme = RepaymentScheme.Linear,
                extraPayments = emptyList(),
            ),
        ),
    )

    val settingsIncremental = SimulationSettings(
        simulationName = "700k Incremental",
        mortgage = mortgage,
        property = Property.NewConstruction(
            initialNotaryPayment = Payment(closingDate, landPrice),
            constructionInstallments = listOf(
                *constructionBillsPayments.toTypedArray<Payment>(),
                *otherBillsPayments.toTypedArray<Payment>(),
                // This completes the original options price estimate
                Payment(date = LocalDate.parse("2025-11-01"), optionsPrice - otherBillsPayments.sumOf { it.amount }),
                Payment(date = LocalDate.parse("2025-11-01"), parkingPrice), // TODO add real date
                // Compensates for bills rounding so it still amounts to the real construction price.
                // In reality, BotBouw will probably adjust the last bill, or the bank will give/take the difference.
                Payment(date = LocalDate.parse("2025-12-31"), constructionPrice - constructionBillsPayments.sumOf { it.amount }),
            ).also { installments ->
                check(installments.sumOf { p -> p.amount } == initialConstructionAccountBalance)
            },
            wozValue = estimatedWozValue,
        ),
    )

    val settingsBulk = SimulationSettings(
        simulationName = "700k Bulk",
        mortgage = mortgage,
        property = Property.NewConstruction(
            initialNotaryPayment = Payment(closingDate, landPrice + parkingPrice + optionsPrice + constructionPrice),
            constructionInstallments = emptyList(),
            wozValue = estimatedWozValue,
        ),
    )
}
