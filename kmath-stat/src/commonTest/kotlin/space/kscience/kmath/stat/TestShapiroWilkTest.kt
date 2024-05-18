/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlin.test.Test

class TestShapiroWilkTest {

    @Test
    fun valueTest() {
        val test_array = listOf(148.0, 154.0, 158.0, 160.0, 161.0, 162.0, 166.0, 170.0, 182.0, 195.0, 236.0)
        val a = MutableList(test_array.size / 2) { 0.0 }
        val y = test_array.sorted()
        val median = y[y.size / 2] // calculate median
        val without_median = test_array.map { it - median }
        println(shapiroWilkTest(x=without_median, a=a, 0))
    }
}