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
     * The price of the storage space in the parking lot, paid later.
     */
    private val storagePrice = 21_600.eur

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
    // /!\ Do not adjust this, it matches the reality of the construction account
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

    /**
     * Total options and deductions paid to BotBouw, including VAT.
     *
     * This includes all little changes like additional sockets, switches, moved walls, minus the returned amounts
     * for the removed kitchen and default wall/ceiling finishing.
     */
    // was 1762 on the contract, but actually calculated this way in the bill
    private val effectiveOptionsBotBouw = (-"1456.21".eur * 121.pct).roundedToTheCent()

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
            amount = (constructionPrice * 10.pct).roundedToTheCent() + effectiveOptionsBotBouw, // 56884.1649 -> 56884.17
            description = "T7: 10% screed floors in private areas (including all additional and discarded work)",
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
            date = "2025-10-29",
            amount = (constructionPrice * 10.pct).roundedToTheCent() - "0.02".eur, // 58646.179 -> 58646.16
            description = "T9: 10% upon delivery (-0.02€ rounding compensation as done by BotBouw)",
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
        Payment(
            date = LocalDate.parse("2025-09-15"),
            amount = "57017.45".eur, // parkingPrice + storagePrice + translator 326.70€ + 'research' 75€ + BTW 21%
            description = "Notary payment for parking & storage",
        ),
        Payment(
            date = LocalDate.parse("2025-11-27"),
            amount = "7356.72".eur,
            description = "Partial Kitchen bill (rest of the construction account)",
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
        parts = listOf(
            MortgagePart(
                id = MortgagePartId("101"),
                term = 30.years,
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
                term = 30.years,
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
            ).also { installments ->
                check(installments.sum() == initialConstructionAccountBalance) {
                    "The initial construction account balance (${initialConstructionAccountBalance.format()}) should " +
                        "match the sum of the installments (${installments.sum().format()})."
                }
            },
            wozValue = estimatedWozValue,
        ),
    )

    val settingsBulk = SimulationSettings(
        simulationName = "700k Bulk",
        mortgage = mortgage,
        property = Property.NewConstruction(
            initialNotaryPayment = Payment(closingDate, landPrice + parkingPrice + storagePrice + optionsPrice + constructionPrice),
            constructionInstallments = emptyList(),
            wozValue = estimatedWozValue,
        ),
    )
}

private fun Iterable<Payment>.sum() = sumOf { it.amount }