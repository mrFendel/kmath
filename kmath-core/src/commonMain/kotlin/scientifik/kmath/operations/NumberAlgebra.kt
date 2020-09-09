package scientifik.kmath.operations

import kotlin.math.abs
import kotlin.math.pow as kpow

/**
 * Advanced Number-like semifield that implements basic operations.
 */
public interface ExtendedFieldOperations<T> :
    FieldOperations<T>,
    TrigonometricOperations<T>,
    HyperbolicOperations<T>,
    PowerOperations<T>,
    ExponentialOperations<T> {
    public override fun tan(arg: T): T = sin(arg) / cos(arg)
    public override fun tanh(arg: T): T = sinh(arg) / cosh(arg)

    public override fun unaryOperation(operation: String, arg: T): T = when (operation) {
        TrigonometricOperations.COS_OPERATION -> cos(arg)
        TrigonometricOperations.SIN_OPERATION -> sin(arg)
        TrigonometricOperations.TAN_OPERATION -> tan(arg)
        TrigonometricOperations.ACOS_OPERATION -> acos(arg)
        TrigonometricOperations.ASIN_OPERATION -> asin(arg)
        TrigonometricOperations.ATAN_OPERATION -> atan(arg)
        HyperbolicOperations.COSH_OPERATION -> cosh(arg)
        HyperbolicOperations.SINH_OPERATION -> sinh(arg)
        HyperbolicOperations.TANH_OPERATION -> tanh(arg)
        HyperbolicOperations.ACOSH_OPERATION -> acosh(arg)
        HyperbolicOperations.ASINH_OPERATION -> asinh(arg)
        HyperbolicOperations.ATANH_OPERATION -> atanh(arg)
        PowerOperations.SQRT_OPERATION -> sqrt(arg)
        ExponentialOperations.EXP_OPERATION -> exp(arg)
        ExponentialOperations.LN_OPERATION -> ln(arg)
        else -> super.unaryOperation(operation, arg)
    }
}

/**
 * Advanced Number-like field that implements basic operations.
 */
public interface ExtendedField<T> : ExtendedFieldOperations<T>, Field<T> {
    public override fun sinh(arg: T): T = (exp(arg) - exp(-arg)) / 2
    public override fun cosh(arg: T): T = (exp(arg) + exp(-arg)) / 2
    public override fun tanh(arg: T): T = (exp(arg) - exp(-arg)) / (exp(-arg) + exp(arg))
    public override fun asinh(arg: T): T = ln(sqrt(arg * arg + one) + arg)
    public override fun acosh(arg: T): T = ln(arg + sqrt((arg - one) * (arg + one)))
    public override fun atanh(arg: T): T = (ln(arg + one) - ln(one - arg)) / 2

    public override fun rightSideNumberOperation(operation: String, left: T, right: Number): T = when (operation) {
        PowerOperations.POW_OPERATION -> power(left, right)
        else -> super.rightSideNumberOperation(operation, left, right)
    }
}

/**
 * Real field element wrapping double.
 *
 * @property value the [Double] value wrapped by this [Real].
 *
 * TODO inline does not work due to compiler bug. Waiting for fix for KT-27586
 */
public inline class Real(public val value: Double) : FieldElement<Double, Real, RealField> {
    public override val context: RealField
        get() = RealField

    public override fun unwrap(): Double = value
    public override fun Double.wrap(): Real = Real(value)

    public companion object
}

/**
 * A field for [Double] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object RealField : ExtendedField<Double>, Norm<Double, Double> {
    public override val zero: Double
        get() = 0.0

    public override val one: Double
        get() = 1.0

    public override fun binaryOperation(operation: String, left: Double, right: Double): Double = when (operation) {
        PowerOperations.POW_OPERATION -> left pow right
        else -> super.binaryOperation(operation, left, right)
    }

    public override inline fun add(a: Double, b: Double): Double = a + b
    public override inline fun multiply(a: Double, k: Number): Double = a * k.toDouble()

    public override inline fun multiply(a: Double, b: Double): Double = a * b

    public override inline fun divide(a: Double, b: Double): Double = a / b

    public override inline fun sin(arg: Double): Double = kotlin.math.sin(arg)
    public override inline fun cos(arg: Double): Double = kotlin.math.cos(arg)
    public override inline fun tan(arg: Double): Double = kotlin.math.tan(arg)
    public override inline fun acos(arg: Double): Double = kotlin.math.acos(arg)
    public override inline fun asin(arg: Double): Double = kotlin.math.asin(arg)
    public override inline fun atan(arg: Double): Double = kotlin.math.atan(arg)

    public override inline fun sinh(arg: Double): Double = kotlin.math.sinh(arg)
    public override inline fun cosh(arg: Double): Double = kotlin.math.cosh(arg)
    public override inline fun tanh(arg: Double): Double = kotlin.math.tanh(arg)
    public override inline fun asinh(arg: Double): Double = kotlin.math.asinh(arg)
    public override inline fun acosh(arg: Double): Double = kotlin.math.acosh(arg)
    public override inline fun atanh(arg: Double): Double = kotlin.math.atanh(arg)

    public override inline fun power(arg: Double, pow: Number): Double = arg.kpow(pow.toDouble())
    public override inline fun exp(arg: Double): Double = kotlin.math.exp(arg)
    public override inline fun ln(arg: Double): Double = kotlin.math.ln(arg)

    public override inline fun norm(arg: Double): Double = abs(arg)

    public override inline fun Double.unaryMinus(): Double = -this
    public override inline fun Double.plus(b: Double): Double = this + b
    public override inline fun Double.minus(b: Double): Double = this - b
    public override inline fun Double.times(b: Double): Double = this * b
    public override inline fun Double.div(b: Double): Double = this / b
}

/**
 * A field for [Float] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object FloatField : ExtendedField<Float>, Norm<Float, Float> {
    public override val zero: Float
        get() = 0.0f

    public override val one: Float
        get() = 1.0f

    public override fun binaryOperation(operation: String, left: Float, right: Float): Float = when (operation) {
        PowerOperations.POW_OPERATION -> left pow right
        else -> super.binaryOperation(operation, left, right)
    }

    public override inline fun add(a: Float, b: Float): Float = a + b
    public override inline fun multiply(a: Float, k: Number): Float = a * k.toFloat()

    public override inline fun multiply(a: Float, b: Float): Float = a * b

    public override inline fun divide(a: Float, b: Float): Float = a / b

    public override inline fun sin(arg: Float): Float = kotlin.math.sin(arg)
    public override inline fun cos(arg: Float): Float = kotlin.math.cos(arg)
    public override inline fun tan(arg: Float): Float = kotlin.math.tan(arg)
    public override inline fun acos(arg: Float): Float = kotlin.math.acos(arg)
    public override inline fun asin(arg: Float): Float = kotlin.math.asin(arg)
    public override inline fun atan(arg: Float): Float = kotlin.math.atan(arg)

    public override inline fun sinh(arg: Float): Float = kotlin.math.sinh(arg)
    public override inline fun cosh(arg: Float): Float = kotlin.math.cosh(arg)
    public override inline fun tanh(arg: Float): Float = kotlin.math.tanh(arg)
    public override inline fun asinh(arg: Float): Float = kotlin.math.asinh(arg)
    public override inline fun acosh(arg: Float): Float = kotlin.math.acosh(arg)
    public override inline fun atanh(arg: Float): Float = kotlin.math.atanh(arg)

    public override inline fun power(arg: Float, pow: Number): Float = arg.kpow(pow.toFloat())
    public override inline fun exp(arg: Float): Float = kotlin.math.exp(arg)
    public override inline fun ln(arg: Float): Float = kotlin.math.ln(arg)

    public override inline fun norm(arg: Float): Float = abs(arg)

    public override inline fun Float.unaryMinus(): Float = -this
    public override inline fun Float.plus(b: Float): Float = this + b
    public override inline fun Float.minus(b: Float): Float = this - b
    public override inline fun Float.times(b: Float): Float = this * b
    public override inline fun Float.div(b: Float): Float = this / b
}

/**
 * A field for [Int] without boxing. Does not produce corresponding ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object IntRing : Ring<Int>, Norm<Int, Int> {
    public override val zero: Int
        get() = 0

    public override val one: Int
        get() = 1

    public override inline fun add(a: Int, b: Int): Int = a + b
    public override inline fun multiply(a: Int, k: Number): Int = k.toInt() * a

    public override inline fun multiply(a: Int, b: Int): Int = a * b

    public override inline fun norm(arg: Int): Int = abs(arg)

    public override inline fun Int.unaryMinus(): Int = -this
    public override inline fun Int.plus(b: Int): Int = this + b
    public override inline fun Int.minus(b: Int): Int = this - b
    public override inline fun Int.times(b: Int): Int = this * b
}

/**
 * A field for [Short] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object ShortRing : Ring<Short>, Norm<Short, Short> {
    public override val zero: Short
        get() = 0

    public override val one: Short
        get() = 1

    public override inline fun add(a: Short, b: Short): Short = (a + b).toShort()
    public override inline fun multiply(a: Short, k: Number): Short = (a * k.toShort()).toShort()

    public override inline fun multiply(a: Short, b: Short): Short = (a * b).toShort()

    public override fun norm(arg: Short): Short = if (arg > 0) arg else (-arg).toShort()

    public override inline fun Short.unaryMinus(): Short = (-this).toShort()
    public override inline fun Short.plus(b: Short): Short = (this + b).toShort()
    public override inline fun Short.minus(b: Short): Short = (this - b).toShort()
    public override inline fun Short.times(b: Short): Short = (this * b).toShort()
}

/**
 * A field for [Byte] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object ByteRing : Ring<Byte>, Norm<Byte, Byte> {
    public override val zero: Byte
        get() = 0

    public override val one: Byte
        get() = 1

    public override inline fun add(a: Byte, b: Byte): Byte = (a + b).toByte()
    public override inline fun multiply(a: Byte, k: Number): Byte = (a * k.toByte()).toByte()

    public override inline fun multiply(a: Byte, b: Byte): Byte = (a * b).toByte()

    public override fun norm(arg: Byte): Byte = if (arg > 0) arg else (-arg).toByte()

    public override inline fun Byte.unaryMinus(): Byte = (-this).toByte()
    public override inline fun Byte.plus(b: Byte): Byte = (this + b).toByte()
    public override inline fun Byte.minus(b: Byte): Byte = (this - b).toByte()
    public override inline fun Byte.times(b: Byte): Byte = (this * b).toByte()
}

/**
 * A field for [Double] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object LongRing : Ring<Long>, Norm<Long, Long> {
    public override val zero: Long
        get() = 0

    public override val one: Long
        get() = 1

    public override inline fun add(a: Long, b: Long): Long = a + b
    public override inline fun multiply(a: Long, k: Number): Long = a * k.toLong()

    public override inline fun multiply(a: Long, b: Long): Long = a * b

    public override fun norm(arg: Long): Long = abs(arg)

    public override inline fun Long.unaryMinus(): Long = (-this)
    public override inline fun Long.plus(b: Long): Long = (this + b)
    public override inline fun Long.minus(b: Long): Long = (this - b)
    public override inline fun Long.times(b: Long): Long = (this * b)
}
