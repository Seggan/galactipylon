package io.github.seggan.galactipylon

import dev.wyck.keys.ResourceKey
import dev.wyck.worldgen.function.DensityFunction
import net.kyori.adventure.key.Key
import kotlin.math.PI
import kotlin.math.abs

const val TAU = 2 * PI

fun Key.asResourceKey() = ResourceKey.of(namespace(), value())

operator fun DensityFunction.plus(other: DensityFunction) = this.add(other)
operator fun DensityFunction.minus(other: DensityFunction) = this.add(other.mul(DensityFunction.constant(-1.0)))
operator fun DensityFunction.times(other: DensityFunction) = this.mul(other)
operator fun DensityFunction.div(other: DensityFunction) = this.mul(other.invert())

inline fun newtonsMethod(
    initialGuess: Double,
    f: (Double) -> Double,
    fp: (Double) -> Double,
    tolerance: Double = 1e-12,
    maxIterations: Int = 20
): Double {
    var guess = initialGuess
    repeat(maxIterations) {
        val delta = f(guess) / fp(guess)
        guess -= delta
        if (abs(delta) < tolerance) {
            return guess
        }
    }
    throw IllegalStateException("Newton iteration failed to converge")
}