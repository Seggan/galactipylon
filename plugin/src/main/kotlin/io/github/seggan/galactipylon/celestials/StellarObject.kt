package io.github.seggan.galactipylon.celestials

import org.bukkit.NamespacedKey
import org.joml.Vector2i

abstract class StellarObject(key: NamespacedKey) : CelestialObject(key) {
    abstract val position: Vector2i
}