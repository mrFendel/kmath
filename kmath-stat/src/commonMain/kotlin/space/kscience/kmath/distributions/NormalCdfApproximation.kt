/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.distributions

import kotlin.math.abs
import kotlin.math.exp

public fun normalCdfApproximation(x: Double, upper: Boolean): Double {
    // Constants
    val ltone = 7.0
    val utzero = 38.0
    val con = 1.28
    val A1 = 0.398942280444
    val A2 = 0.399903438504
    val A3 = 5.75885480458
    val A4 = 29.8213557808
    val A5 = 2.62433121679
    val A6 = 48.6959930692
    val A7 = 5.92885724438
    val B1 = 0.398942280385
    val B2 = 3.8052e-8
    val B3 = 1.00000615302
    val B4 = 3.98064794e-4
    val B5 = 1.98615381364
    val B6 = 0.151679116635
    val B7 = 5.29330324926
    val B8 = 4.8385912808
    val B9 = 15.1508972451
    val B10 = 0.742380924027
    val B11 = 30.789933034
    val B12 = 3.99019417011

    // Variables
    var z = x
    var temp: Double
    val y: Double

    val isUpper = if (z > 0) upper else !upper
    z = abs(z)

    if (!((z <= ltone) || (isUpper && z <= utzero))) {
        return if (isUpper) 0.0 else 1.0
    }

    y = 0.5 * z * z

    temp = if (z <= con) {
        // For z <= con, use polynomial approximation
        0.5 - z * (A1 - A2 * y / (y + A3 - A4 / (y + A5 + A6 / (y + A7))))
    } else {
        // For larger z, use rational approximation
        B1 * exp(-y) / (z - B2 + B3 / (z + B4 + B5 / (z - B6 + B7 /
                (z + B8 - B9 / (z + B10 + B11 / (z + B12))))))
    }

    return if (isUpper) temp else 1 - temp
}