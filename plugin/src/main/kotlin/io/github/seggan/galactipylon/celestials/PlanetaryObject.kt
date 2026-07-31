package io.github.seggan.galactipylon.celestials

import io.github.seggan.galactipylon.celestials.property.Orbit
import org.bukkit.NamespacedKey

abstract class PlanetaryObject(key: NamespacedKey) : CelestialObject(key) {
    abstract val orbit: Orbit
}