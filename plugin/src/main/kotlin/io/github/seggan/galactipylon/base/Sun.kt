package io.github.seggan.galactipylon.base

import io.github.seggan.galactipylon.celestials.StellarObject
import io.github.seggan.galactipylon.galacticKey
import org.bukkit.Material
import org.joml.Vector2d

object Sun : StellarObject(galacticKey("sun")) {
    override val position = Vector2d(0.0, 0.0)

    override val displayMaterial = Material.YELLOW_WOOL

    override val mass = 1.988e30
}