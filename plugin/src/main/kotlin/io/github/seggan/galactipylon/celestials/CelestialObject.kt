package io.github.seggan.galactipylon.celestials

import io.github.seggan.galactipylon.GalactipylonRegistry
import org.bukkit.Keyed
import org.bukkit.NamespacedKey

sealed class CelestialObject(private val key: NamespacedKey) : Keyed {

    val orbiters: List<PlanetaryObject>
        get() = GalactipylonRegistry.CELESTIAL_OBJECTS
            .filterIsInstance<PlanetaryObject>()
            .filter { it.orbit.parent == this }


    override fun getKey() = key

    open fun register() {
        GalactipylonRegistry.CELESTIAL_OBJECTS.register(this)
    }
}