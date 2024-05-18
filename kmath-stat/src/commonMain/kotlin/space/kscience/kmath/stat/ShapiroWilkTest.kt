/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.distributions.normalCdfApproximation
import kotlin.math.*


/**
 * Helper function for Shapiro-Wilk test.
 * Evaluates the percent point function of the standard normal distribution.
 * Unused ifault return is commented out from return statements.
 */
private fun ppnd(p: Double): Double {
    // Constants
    val split = 0.42
    val A0 = 2.50662823884
    val A1 = -18.61500062529
    val A2 = 41.39119773534
    val A3 = -25.44106049637
    val B1 = -8.47351093090
    val B2 = 23.08336743743
    val B3 = -21.06224101826
    val B4 = 3.13082909833
    val C0 = -2.78718931138
    val C1 = -2.29796479134
    val C2 = 4.85014127135
    val C3 = 2.32121276858
    val D1 = 3.54388924762
    val D2 = 1.63706781897

    // Variables
    val q = p - 0.5
    if (abs(q) <= split) {
        val r = q * q
        var temp = q * (((A3 * r + A2) * r + A1) * r + A0)
        temp /= ((((B4 * r + B3) * r + B2) * r + B1) * r + 1.0)
        return temp
    }

    var r = p
    if (q > 0) {
        r = 1 - p
    }
    if (r > 0) {
        r = sqrt(-log(r, E))
    } else {
        return 0.0
    }

    var temp = (((C3 * r + C2) * r + C1) * r + C0)
    temp /= (D2 * r + D1) * r + 1.0
    return if (q < 0) -temp else temp
}

private fun poly(c: List<Double>, nord: Int, x: Double): Double {
    var res: Double = c[0]
    if (nord == 1) {
        return res
    }

    var p = x * c[nord - 1]
    if (nord == 2) {
        return res + p
    }

    for (ind in nord - 2 downTo 1) {
        p = (p + c[ind]) * x
    }
    res += p
    return res
}


public data class ShapiroWilkTestResult(
    val w: Double = 1.0,
    val pw: Double = 1.0,
    val ifault: Int = 1
)

public fun shapiroWilkTest(
    x: List<Double>,
    a: MutableList<Double>,
    init: Int = 0,
    n11: Int = -1
): ShapiroWilkTestResult {

    val n: Int = x.size
    val n2 = a.size
    var isInitialized = if (init !=0) true else false
    val upper = true

    val c1 = listOf(0.0, 0.221157, -0.147981, -0.207119e1, 0.4434685e1, -0.2706056e1)
    val c2 = listOf(0.0, 0.42981e-1, -0.293762, -0.1752461e1, 0.5682633e1, -0.3582633e1)
    val c3 = listOf(0.5440, -0.39978, 0.25054e-1, -0.6714e-3)
    val c4 = listOf(0.13822e1, -0.77857, 0.62767e-1, -0.20322e-2)
    val c5 = listOf(-0.15861e1, -0.31082, -0.83751e-1, 0.38915e-2)
    val c6 = listOf(-0.4803, -0.82676e-1, 0.30302e-2)
    val c7 = listOf(0.164, 0.533)
    val c8 = listOf(0.1736, 0.315)
    val c9 = listOf(0.256, -0.635e-2)
    val g = listOf(-0.2273e1, 0.459)
    val Z90 = 0.12816e1
    val Z95 = 0.16449e1
    val Z99 = 0.23263e1
    val ZM = 0.17509e1
    val ZSS = 0.56268
    val BF1 = 0.8378
    val XX90 = 0.556
    val XX95 = 0.622
    val SQRTH = sqrt(2.0) / 2
    val PI6 = 6 / PI
    val SMALL = 1e-19
    var w = 1.0
    var pw = 1.0
    var an = n.toDouble()
    var n1 = n11
    var nn2 = n / 2
    if (nn2 < n2) return ShapiroWilkTestResult(1.0, 1.0, 3)
    if (n < 3) return ShapiroWilkTestResult()
    if (n1 < 0) n1 = n

    if (!isInitialized) {
        if (n == 3) {
            a[0] = SQRTH
        } else {
            var an25 = an + 0.25
            var summ2 = 0.0
            for (ind1 in 0 until n2) {
                val temp = ppnd((ind1 + 1 - 0.375) / an25)
                a[ind1] = temp
                summ2 += temp * temp
            }
            summ2 *= 2.0
            val ssumm2 = sqrt(summ2)
            val rsn = 1 / sqrt(an)
            var A1 = poly(c1, 6, rsn) - (a[0] / ssumm2)
            var i1: Int
            val fac: Double
            if (n > 5) {
                i1 = 2
                val A2 = -a[1] / ssumm2 + poly(c2, 6, rsn)
                fac = sqrt((summ2 - 2 * a[0] * a[0] - 2 * a[1] * a[1]) / (1 - 2 * A1 * A1 - 2 * A2 * A2))
                a[1] = A2
            } else {
                i1 = 1
                fac = sqrt((summ2 - 2 * a[0] * a[0]) / (1 - 2 * A1 * A1))
            }
            a[0] = A1
            for (ind1 in i1 until nn2) {
                a[ind1] *= -1.0 / fac
            }
        }
        isInitialized = true
    }


    val n1Adjusted: Int = x.size

    if (n1Adjusted < 3) {
        return ShapiroWilkTestResult(w, pw, 1)
    }
    n1 = n1Adjusted
    val ncens = n - n1Adjusted

    if (ncens < 0 || (ncens > 0 && n < 20)) return ShapiroWilkTestResult(w, pw, 4)
    val delta = ncens / an

    if (delta > 0.8) return ShapiroWilkTestResult(w, pw, 5)

    var RANGE = x[n1Adjusted - 1] - x[0]
    if (RANGE < SMALL) return ShapiroWilkTestResult(w, pw, 6)

    var XX = x[0] / RANGE
    var SX = XX
    var SA = -a[0]
    var ind2 = n - 2
    for (ind1 in 1 until n1) {
        val XI = x[ind1] / RANGE
        SX += XI
        if (ind1 != ind2) {
            val minIndex = if (ind1 < ind2) ind1 else ind2
            SA += (if (ind1 < ind2) -1 else 1) * a[minIndex]
        }
        XX = XI
        ind2 -= 1
    }

    var ifault = 0
    if (n > 5000) ifault = 2

    SA /= n1
    SX /= n1
    var SSA = 0.0
    var SSX = 0.0
    var SAX = 0.0
    ind2 = n - 1
    for (ind1 in 0 until n1) {
        val ASA = if (ind1 != ind2) {
            val minIndex = if (ind1 < ind2) ind1 else ind2
            (-1).toDouble().pow(if (ind1 < ind2) 1 else 0) * a[minIndex] - SA
        } else {
            -SA
        }
        val XSX = x[ind1] / RANGE - SX
        SSA += ASA * ASA
        SSX += XSX * XSX
        SAX += ASA * XSX
        ind2--
    }


    val SSASSX = sqrt(SSA * SSX)
    val w1 = (SSASSX - SAX) * (SSASSX + SAX) / (SSA * SSX)
    w = 1 - w1

    if (n == 3) {
        if (w < 0.75) return ShapiroWilkTestResult(0.75, 0.0, ifault)
        pw = 1 - PI6 * acos(sqrt(w))
        return ShapiroWilkTestResult(w, pw, ifault)
    }

    var y = log(w1, E)
    XX = log(an, E)
    val gamma = poly(g, 2, an)
    var m: Double
    var s: Double

    if (n <= 11) {
        if (y >= gamma) {
            return ShapiroWilkTestResult(w, SMALL, ifault)
        }
        y = -log(gamma - y, E)
        m = poly(c3, 4, an)
        s = exp(poly(c4, 4, an))
    } else {
        m = poly(c5, 4, XX)
        s = exp(poly(c6, 3, XX))
    }

    if (ncens > 0) {
        val ld = -log(delta, E)
        val bf = 1 + XX * BF1
        val Z90F = Z90 + bf * poly(c7, 2, XX90.pow(XX))
        val Z95F = Z95 + bf * poly(c8, 2, XX95.pow(XX))
        val Z99F = Z99 + bf * poly(c9, 2, XX.pow(XX))
        val ZFM = (Z90F + Z95F + Z99F) / 3
        val ZSD = (Z90 * (Z90F - ZFM) + Z95 * (Z95F - ZFM) + Z99 * (Z99F - ZFM)) / ZSS
        val ZBAR = ZFM - ZSD * ZM
        m += ZBAR * s
        s *= ZSD
    }
    pw = normalCdfApproximation((y - m) / s, upper)
    return ShapiroWilkTestResult(w, pw, ifault)
}