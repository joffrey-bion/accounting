package org.hildan.accounting.mortgage

import org.hildan.accounting.money.Amount
import org.hildan.accounting.money.pct
import org.hildan.accounting.money.eur
import org.hildan.accounting.mortgage.interest.ApplicableInterestRate
import org.hildan.accounting.mortgage.interest.DayCountConvention
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RepaymentSchemeTest {

    @Test
    fun linear_basicDivision() {
        val monthly = RepaymentScheme.Linear.principalRepayment(
            balance = "1000".eur,
            interestRate = ApplicableInterestRate(5.pct, DayCountConvention.ThirtyE360),
            remainingMonths = 10,
        )
        assertEquals("100".eur, monthly)
    }

    @Test
    fun linear_remainingMonths1_returnsFullBalance() {
        val single = RepaymentScheme.Linear.principalRepayment(
            balance = "123456".eur,
            interestRate = ApplicableInterestRate(3.pct, DayCountConvention.ThirtyE360),
            remainingMonths = 1,
        )
        assertEquals("123456".eur, single)
    }

    @Test
    fun linear_zeroBalance_returnsZero() {
        val monthly = RepaymentScheme.Linear.principalRepayment(
            balance = Amount.ZERO,
            interestRate = ApplicableInterestRate(7.pct, DayCountConvention.ThirtyE360),
            remainingMonths = 24,
        )
        assertEquals(Amount.ZERO, monthly)
    }

    @Test
    fun linear_independentOfInterestRateAndDcc() {
        val balance = "9999".eur
        val months = 9
        val res1 = RepaymentScheme.Linear.principalRepayment(
            balance = balance,
            interestRate = ApplicableInterestRate(2.pct, DayCountConvention.ThirtyE360),
            remainingMonths = months,
        )
        val res2 = RepaymentScheme.Linear.principalRepayment(
            balance = balance,
            interestRate = ApplicableInterestRate(25.pct, DayCountConvention.ThirtyE360ISDA),
            remainingMonths = months,
        )
        val res3 = RepaymentScheme.Linear.principalRepayment(
            balance = balance,
            interestRate = ApplicableInterestRate(12.pct, DayCountConvention.ActualActual),
            remainingMonths = months,
        )
        assertEquals(res1, res2)
        assertEquals(res1, res3)
        assertEquals("1111".eur, res1) // 9999 / 9
    }

    @Test
    fun annuity_constantTotalPayment() {
        val interestRate = ApplicableInterestRate(6.pct, DayCountConvention.ThirtyE360)
        val initialBalance = "100000".eur
        val expectedTotalMonthly = "8606.64".eur

        var principal = initialBalance
        repeat(12) { nPastMonths ->
            val interest = principal * interestRate.annualRate / 12
            val principalRepayment = RepaymentScheme.Annuity.principalRepayment(
                balance = principal,
                interestRate = interestRate,
                remainingMonths = 12 - nPastMonths,
            )
            val totalPayment = principalRepayment + interest
            assertEquals(expectedTotalMonthly, totalPayment.roundedToTheCent(), "total monthly should be constant")
            principal -= principalRepayment
        }
    }

    @Test
    fun annuity_remainingMonths1_returnsFullBalance() {
        val balance = "54321".eur
        val actual = RepaymentScheme.Annuity.principalRepayment(
            balance = balance,
            interestRate = ApplicableInterestRate(8.pct, DayCountConvention.ThirtyE360),
            remainingMonths = 1,
        )
        // Allow tiny numeric noise by asserting equality at cent precision
        assertEquals(balance.roundedToTheCent(), actual.roundedToTheCent())
    }

    @Test
    fun annuity_zeroBalance_returnsZero() {
        val actual = RepaymentScheme.Annuity.principalRepayment(
            balance = Amount.ZERO,
            interestRate = ApplicableInterestRate(5.pct, DayCountConvention.ThirtyE360),
            remainingMonths = 360,
        )
        assertEquals(Amount.ZERO, actual)
    }

    @Test
    fun annuity_monotonicity_withDecreasingMonths() {
        val balance = "50000".eur
        val r = ApplicableInterestRate(5.pct, DayCountConvention.ThirtyE360)
        val p120 = RepaymentScheme.Annuity.principalRepayment(balance, r, remainingMonths = 120)
        val p60 = RepaymentScheme.Annuity.principalRepayment(balance, r, remainingMonths = 60)
        val p12 = RepaymentScheme.Annuity.principalRepayment(balance, r, remainingMonths = 12)
        assertTrue(p60 > p120, "principal should increase when remaining months decrease")
        assertTrue(p12 > p60, "principal should increase when remaining months decrease")
    }

    @Test
    fun annuity_actualActual_throws() {
        assertFailsWith<IllegalArgumentException> {
            RepaymentScheme.Annuity.principalRepayment(
                balance = "100000".eur,
                interestRate = ApplicableInterestRate(5.pct, DayCountConvention.ActualActual),
                remainingMonths = 240,
            )
        }
    }
}
