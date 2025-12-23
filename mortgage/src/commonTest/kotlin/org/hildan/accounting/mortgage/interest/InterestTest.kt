package org.hildan.accounting.mortgage.interest

import kotlinx.datetime.LocalDate
import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.eur
import org.hildan.accounting.money.pct
import org.hildan.accounting.mortgage.Payment
import org.hildan.accounting.mortgage.PaymentPeriod
import kotlin.test.Test
import kotlin.test.assertEquals

class InterestTest {

    @Test
    fun test_noBalanceReductions() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 1000.eur,
            balanceReductions = emptyList(),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 4, 1))
        )
        assertEqualsToScale10(1000.eur * 6.pct * 31 / 365, actualInterest)
    }

    @Test
    fun test_zeroBalanceReduction() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 1000.eur,
            balanceReductions = listOf(Payment(LocalDate(2023, 3, 10), 0.eur)),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 4, 1))
        )
        assertEqualsToScale10(1000.eur * 6.pct * 31 / 365, actualInterest)
    }

    @Test
    fun test_singleBalanceReduction() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 1000.eur,
            balanceReductions = listOf(Payment(LocalDate(2023, 3, 10), 100.eur)),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 4, 1))
        )
        assertEqualsToScale10(
            1000.eur * 6.pct * 9 / 365 +
                900.eur * 6.pct * (31 - 9) / 365,
            actualInterest,
        )
    }

    @Test
    fun test_multipleBalanceReductions() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 2000.eur,
            balanceReductions = listOf(
                Payment(LocalDate(2023, 3, 5), 500.eur),
                Payment(LocalDate(2023, 3, 15), 300.eur)
            ),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 4, 1))
        )
        assertEqualsToScale10(
            2000.eur * 6.pct * 4 / 365 +
                1500.eur * 6.pct * 10 / 365 +
                1200.eur * 6.pct * 17 / 365,
            actualInterest,
        )
    }

    @Test
    fun test_balanceReductionOnLastDay() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 1500.eur,
            balanceReductions = listOf(Payment(LocalDate(2023, 3, 31), 500.eur)),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 4, 1))
        )
        assertEqualsToScale10(
            1500.eur * 6.pct * 30 / 365 +
                1000.eur * 6.pct * 1 / 365,
            actualInterest,
        )
    }

    @Test
    fun test_irregularDaysInPeriod() {
        val actualInterest = ApplicableInterestRate(
            annualRate = 6.pct,
            dayCountConvention = DayCountConvention.ActualActual
        ).interestByPartsOn(
            initialBalance = 1000.eur,
            balanceReductions = listOf(Payment(LocalDate(2023, 3, 18), 200.eur)),
            period = PaymentPeriod(LocalDate(2023, 3, 1), LocalDate(2023, 3, 20))
        )
        assertEqualsToScale10(
            1000.eur * 6.pct * 17 / 365 +
                800.eur * 6.pct * 2 / 365,
            actualInterest,
        )
    }

    private fun assertEqualsToScale10(expected: Amount, actual: Amount) {
        assertEquals(expected.roundedToScale(10), actual.roundedToScale(10))
    }
}