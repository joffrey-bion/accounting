package org.hildan.accounting.money

import kotlin.test.*

class MoneyTest {

    @Test
    fun negate() {
        assertEquals((-0).eur, -(0.eur))
        assertEquals((-20).eur, -(20.eur))
        assertEquals((-50).eur, -(50.eur))
        assertEquals((-123456789).eur, -(123456789.eur))
        assertEquals((-1234567890000000000).eur, -(1234567890000000000.eur))
    }

    @Test
    fun negate_negative() {
        assertEquals(0.eur, -((-0).eur))
        assertEquals(20.eur, -((-20).eur))
        assertEquals(50.eur, -((-50).eur))
    }

    @Test
    fun add() {
        assertEquals(20.eur, 20.eur + 0.eur)
        assertEquals(20.eur, 15.eur + 5.eur)
        assertEquals("15.5".eur, 15.eur + "0.5".eur)
        assertEquals("15.1".eur, 15.eur + "0.1".eur)
    }

    @Test
    fun add_negative() {
        assertEquals(20.eur, 20.eur + (-0).eur)
        assertEquals(10.eur, 15.eur + (-5).eur)
        assertEquals("14.5".eur, 15.eur + "-0.5".eur)
        assertEquals("14.9".eur, 15.eur + "-0.1".eur)
    }

    @Test
    fun add_negativeResult() {
        assertEquals((-20).eur, (-20).eur + (-0).eur)
        assertEquals((-10).eur, 15.eur + (-25).eur)
        assertEquals("-14.5".eur, (-10).eur + "-4.5".eur)
        assertEquals("14.9".eur, 15.eur + "-0.1".eur)
    }

    @Test
    fun add_beyondLongRange() {
        assertEquals("9223372036854775808".eur, Long.MAX_VALUE.toAmount() + 1.eur)
        assertEquals("1234567890000015000".eur, 1234567890000000000.eur + 15000.eur)
        assertEquals("1234567890123456789".eur, 1234567890000000000.eur + 123456789.eur)
    }

    @Test
    fun subtract() {
        assertEquals(20.eur, 20.eur - 0.eur)
        assertEquals(10.eur, 15.eur - 5.eur)
        assertEquals("14.5".eur, 15.eur - "0.5".eur)
        assertEquals("14.9".eur, 15.eur - "0.1".eur)
    }

    @Test
    fun subtract_beyondLongRange() {
        assertEquals("-9223372036854775809".eur, Long.MIN_VALUE.toAmount() - 1.eur)
        assertEquals("-1234567890000015000".eur, (-1234567890000000000).eur - 15000.eur)
        assertEquals("-1234567890123456789".eur, (-1234567890000000000).eur - 123456789.eur)
    }

    @Test
    fun multiply_by_positiveInt() {
        assertEquals(0.eur, 20.eur * 0)
        assertEquals(30.eur, 15.eur * 2)
        assertEquals("10.5".eur, "3.5".eur * 3)
        assertEquals("0.7".eur, "0.1".eur * 7)
    }

    @Test
    fun multiply_by_negativeInt() {
        assertEquals(0.eur, 20.eur * -0)
        assertEquals((-30).eur, 15.eur * -2)
        assertEquals("-10.5".eur, "3.5".eur * -3)
        assertEquals("-0.7".eur, "0.1".eur * -7)
    }

    @Test
    fun multiply_negative_by_positiveInt() {
        assertEquals(0.eur, (-20).eur * 0)
        assertEquals((-30).eur, (-15).eur * 2)
        assertEquals("-10.5".eur, "-3.5".eur * 3)
        assertEquals("-0.7".eur, "-0.1".eur * 7)
    }

    @Test
    fun multiply_by_fraction() {
        assertEquals(0.eur, 42.eur * 0.pct)
        assertEquals(10.eur, 20.eur * 50.pct)
        assertEquals((-30).eur, (-100).eur * 30.pct)
        assertEquals((-30).eur, 100.eur * (-30).pct)
        assertEquals("11.5".eur, 50.eur * 23.pct)
        assertEquals("-0.007".eur, 7.eur * "-0.1".pct)
    }

    @Test
    fun divide() {
        assertEquals(0.eur, 0.eur / 3)
        assertEquals(5.eur, 15.eur / 3)
        assertEquals("33.3333".eur, 100.eur / 3) // scale is 4, with ROUND_HALF_AWAY_FROM_ZERO
        assertEquals("0.15".eur, 15.eur / 100)
        assertEquals((-16).eur, (-128).eur / 8)
    }

    @Test
    fun divideByZero_shouldThrow() {
        assertFailsWith<ArithmeticException> {
           42.eur / 0
        }
    }

    @Test
    fun format_defaultNumberOfDigits() {
        assertEquals("42.00", 42.eur.format())
        assertEquals("-42.00", (-42).eur.format())
        assertEquals("1.23", "1.2345".eur.format())
        assertEquals("1.24", "1.2356".eur.format())
    }

    @Test
    fun format_customNumberOfDigits() {
        assertEquals("1", "1.2356".eur.format(nDigitsAfterDot = 0))
        assertEquals("1.2", "1.2356".eur.format(nDigitsAfterDot = 1))
        assertEquals("1.24", "1.2356".eur.format(nDigitsAfterDot = 2))
        assertEquals("1.236", "1.2356".eur.format(nDigitsAfterDot = 3))
        assertEquals("1.2356", "1.2356".eur.format(nDigitsAfterDot = 4))
    }

    @Test
    fun format_failsWithNegativeNumberOfDigits() {
        assertFailsWith<ArithmeticException> {
            123.eur.format(nDigitsAfterDot = -1)
        }
    }
}
