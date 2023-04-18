/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.DoubleField.pow
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.fold


// TODO: add p-value with formula: 2*(1 - cdf(|zScore|))
public data class VarianceRatioTestResult(val varianceRatio: Double=1.0, val zScore: Double=0.0)
    /**
     * Container class for Variance Ratio Test result:
     * ratio itself, corresponding Z-score, also it's p-value
     * **/

public fun varianceRatioTest(series: Series<Double>, shift: Int, homoscedastic: Boolean=true): VarianceRatioTestResult {

    /**
     * Calculates the Z-statistic and the p-value for the Lo and MacKinlay's Variance Ratio test (1987)
     * under Homoscedastic or Heteroscedstic assumptions
     * 	https://ssrn.com/abstract=346975
     * **/

    require(shift > 1) {"Shift must be greater than one"}
    require(shift < series.size) {"Shift must be smaller than sample size"}
    val sum = { x: Double, y: Double -> x + y }

    with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
        val mean = series.fold(0.0, sum) / series.size
        val demeanedSquares = series.map { power(it - mean, 2) }
        val variance = demeanedSquares.fold(0.0, sum)
        if (variance == 0.0) return VarianceRatioTestResult()


        var seriesAgg = series
        for (i in 1..<shift) {
            seriesAgg = seriesAgg.zip(series.moveTo(i)) { v1, v2 -> v1 + v2 }
        }

        val demeanedSquaresAgg = seriesAgg.map { power(it - shift * mean, 2) }
        val varianceAgg = demeanedSquaresAgg.fold(0.0, sum)

        val varianceRatio =
            varianceAgg * (series.size.toDouble() - 1) / variance / (series.size.toDouble() - shift.toDouble() + 1) / (1 - shift.toDouble()/series.size.toDouble()) / shift.toDouble()


        // calculating asymptotic variance
        val phi = if (homoscedastic) {  // under homoscedastic null hypothesis
            2 * (2 * shift - 1.0) * (shift - 1.0) / (3 * shift * series.size)
        } else { // under heteroscedastic null hypothesis
            var accumulator = 0.0
            for (j in 1..<shift) {
                val temp = demeanedSquares
                val delta = series.size * temp.zipWithShift(j) { v1, v2 -> v1 * v2 }.fold(0.0, sum) / variance.pow(2)
                accumulator += delta * 4 * (shift - j).toDouble().pow(2) / shift.toDouble().pow(2)
            }
            accumulator
        }

        val zScore = (varianceRatio - 1) / phi.pow(0.5)
        return VarianceRatioTestResult(varianceRatio, zScore)
    }
}
