package io.github.seggan.galactipylon.api

import io.github.seggan.galactipylon.GalactipylonRegistry
import org.bukkit.Keyed
import org.bukkit.NamespacedKey

abstract class CelestialObject(private val key: NamespacedKey) : Keyed {

    override fun getKey() = key

    open fun register() {
        GalactipylonRegistry.CELESTIAL_OBJECTS.register(this)
    }
}