package org.hildan.accounting.testdata

import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.datetime.*
import org.hildan.accounting.money.*
import org.hildan.accounting.mortgage.*
import org.hildan.accounting.mortgage.Property

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
    private val closingDate = LocalDate(year = 2023, monthNumber = 11, dayOfMonth = 17)

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
    )

    // These dates are not the dates of the bills, but the dates at which the bank released the funds and paid.
    private val constructionBillsPayments = listOf(
        bill("2024-01-16", constructionPrice * 3.pct, description = "T1: 3% construction start"),
        bill("2024-04-18", constructionPrice * 10.pct, description = "T2: 10% rough lowest floor complete"),
        // Note: both T3 and T4 were paid in the same month, and the bank released the funds in one go on June 18th.
        bill("2024-06-18", constructionPrice * (15.pct + 10.pct), description = "T3 (15% rough floor in private area) + T4 (10% inner cavity leaves in private area)"),
        bill("2024-11-26", constructionPrice * 10.pct + -("1456.21".eur) * (21.pct + 1), description = "T7: 10% screed floors in private areas (excluding discarded work KMW36 with 21%VAT)"),
        bill("2024-12-13", "137818.52".eur, description = "T6: 23.5% watertight roof"), // we don't apply the percentage because it's incorrectly rounded
        bill("2025-01-15", constructionPrice * 5.pct, description = "T5: 5% exterior walls of the private part"), // TODO add real date
        bill("2025-05-01", constructionPrice * "13.5".pct, description = "T8"), // TODO add real date
        bill("2025-09-01", constructionPrice * 10.pct, description = "on delivery date"), // TODO add real date
    )

    private fun bill(
        date: String,
        amount: Amount,
        description: String,
    ): Payment = Payment(
        date = LocalDate.parse(date),
        // BotBouw rounds the numbers up even when less than 0.5
        amount = amount.roundedToTheCent(RoundingMode.CEILING),
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
                    Payment(LocalDate.parse("2024-06-06"), 50_000.eur, "Extra payment"),
                    Payment(LocalDate.parse("2024-06-12"), "12202.92".eur, "Complete to make a whole percentage"),
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
        dayCountConvention = DayCountConvention.ThirtyE360ISDA,
    )

    val settingsIncremental = SimulationSettings(
        simulationName = "700k Incremental",
        mortgage = mortgage,
        property = Property.NewConstruction(
            initialNotaryPayment = Payment(closingDate, landPrice),
            constructionInstallments = listOf(
                Payment(date = LocalDate.parse("2025-03-01"), parkingPrice), // TODO add real date
                Payment(date = LocalDate.parse("2025-07-01"), optionsPrice), // TODO add real date
                *constructionBillsPayments.toTypedArray<Payment>(),
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
