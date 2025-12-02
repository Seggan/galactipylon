package io.github.seggan.galactipylon.celestials

import io.github.seggan.galactipylon.GalactipylonRegistry
import io.github.seggan.galactipylon.celestials.property.Orbit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey

abstract class CelestialObject(private val key: NamespacedKey) : Keyed {

    abstract val orbit: Orbit

    override fun getKey() = key

    open fun register() {
        GalactipylonRegistry.CELESTIAL_OBJECTS.register(this)
    }
}