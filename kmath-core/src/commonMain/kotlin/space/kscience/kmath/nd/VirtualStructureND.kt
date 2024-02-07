/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI

public open class VirtualStructureND<T>(
    override val type: SafeType<T>,
    override val shape: ShapeND,
    public val producer: (IntArray) -> T,
) : StructureND<T> {

    @PerformancePitfall
    override fun get(index: IntArray): T {
        requireIndexInShape(index, shape)
        return producer(index)
    }
}

@UnstableKMathAPI
public class VirtualDoubleStructureND(
    shape: ShapeND,
    producer: (IntArray) -> Double,
) : VirtualStructureND<Double>(safeTypeOf(), shape, producer)

@UnstableKMathAPI
public class VirtualIntStructureND(
    shape: ShapeND,
    producer: (IntArray) -> Int,
) : VirtualStructureND<Int>(safeTypeOf(), shape, producer)