package io.github.seggan.galactipylon

import dev.wyck.keys.ResourceKey
import dev.wyck.worldgen.function.DensityFunction
import net.kyori.adventure.key.Key

fun Key.asResourceKey() = ResourceKey.of(namespace(), value())

operator fun DensityFunction.plus(other: DensityFunction) = this.add(other)
operator fun DensityFunction.minus(other: DensityFunction) = this.add(other.mul(DensityFunction.constant(-1.0)))
operator fun DensityFunction.times(other: DensityFunction) = this.mul(other)
operator fun DensityFunction.div(other: DensityFunction) = this.mul(other.invert())