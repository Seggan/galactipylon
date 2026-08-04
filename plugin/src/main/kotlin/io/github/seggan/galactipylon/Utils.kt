package io.github.seggan.galactipylon

import dev.wyck.keys.ResourceKey
import dev.wyck.worldgen.function.DensityFunction
import net.kyori.adventure.key.Key

fun Key.asResourceKey() = ResourceKey.of(namespace(), value())

operator fun DensityFunction.plus(other: DensityFunction) = this.add(other)
operator fun DensityFunction.times(other: DensityFunction) = this.mul(other)