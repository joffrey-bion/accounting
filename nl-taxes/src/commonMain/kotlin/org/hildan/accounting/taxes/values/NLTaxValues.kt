package org.hildan.accounting.taxes.values

import org.hildan.accounting.money.eur
import org.hildan.accounting.money.pct

/**
 * These values are defined by the Dutch government and change every year.
 *
 * Check out the README of the project for how to update them.
 */
internal object NLTaxValues {
    fun forYear(year: Int) = when(year) {
        2021 -> taxParams {
            a2_1 = 35_129.eur
            a3_1 = 68_507.eur
            b1_1 = "37.10".pct
            b2_1 = "37.10".pct
            b3_1 = "49.50".pct
            specialRewardsTaxRate = "55.50".pct

            ahkm1_1 = 2837.eur
            ahkg1 = 21043.eur
            ahka1_1 = "5.977".pct

            arko1_1 = "4.581".pct
            arko2_1 = "28.771".pct
            arko3_1 = "2.663".pct
            arka4_1 = "6.000".pct
            arkg1 = 10_108.eur
            arkg2 = 21_835.eur
            arkg3 = 35_652.eur
            arkg4 = 105_735.eur
        }
        2022 -> taxParams {
            a2_1 = 35_472.eur
            a3_1 = 69_398.eur
            b1_1 = "37.07".pct
            b2_1 = "37.07".pct
            b3_1 = "49.50".pct
            specialRewardsTaxRate = "55.36".pct

            ahkm1_1 = 2888.eur
            ahkg1 = 21316.eur
            ahka1_1 = "6.007".pct

            arko1_1 = "4.541".pct
            arko2_1 = "28.461".pct
            arko3_1 = "2.610".pct
            arka4_1 = "5.860".pct
            arkg1 = 10_350.eur
            arkg2 = 22_356.eur
            arkg3 = 36_649.eur
            arkg4 = 109_346.eur
        }
        2023 -> taxParams {
            a2_1 = 37_149.eur
            a3_1 = 73_031.eur
            b1_1 = "36.93".pct
            b2_1 = "36.93".pct
            b3_1 = "49.50".pct
            specialRewardsTaxRate = "56.01".pct

            ahkm1_1 = 3070.eur
            ahkg1 = 22660.eur
            ahka1_1 = "6.095".pct

            arko1_1 = "8.231".pct
            arko2_1 = "29.861".pct
            arko3_1 = "3.085".pct
            arka4_1 = "6.510".pct
            arkg1 = 10_741.eur
            arkg2 = 23_201.eur
            arkg3 = 37_691.eur
            arkg4 = 115_295.eur
        }
        2024 -> taxParams {
            a2_1 = 38_098.eur
            a3_1 = 75_518.eur
            b1_1 = "36.97".pct
            b2_1 = "36.97".pct
            b3_1 = "49.50".pct
            specialRewardsTaxRate = "56.01".pct

            ahkm1_1 = 3362.eur
            ahkg1 = 24_812.eur
            ahka1_1 = "6.630".pct

            arko1_1 = "8.425".pct
            arko2_1 = "31.433".pct
            arko3_1 = "2.471".pct
            arka4_1 = "6.510".pct
            arkg1 = 11_490.eur
            arkg2 = 24_820.eur
            arkg3 = 39_957.eur
            arkg4 = 124_934.eur
        }
        2025 -> taxParams {
            a2_1 = 38_441.eur
            a3_1 = 76_817.eur
            b1_1 = "35.82".pct
            b2_1 = "37.48".pct
            b3_1 = "49.50".pct
            specialRewardsTaxRate = "56.01".pct

            ahkm1_1 = 3068.eur
            ahkg1 = 28_406.eur
            ahka1_1 = "6.337".pct

            arko1_1 = "8.053".pct
            arko2_1 = "30.030".pct
            arko3_1 = "2.258".pct
            arka4_1 = "6.510".pct
            arkg1 = 12_169.eur
            arkg2 = 26_288.eur
            arkg3 = 43_071.eur
            arkg4 = 129_078.eur
        }
        else -> error("No information on tax parameters for year $year")
    }
}
