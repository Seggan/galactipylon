package io.github.seggan.galactipylon.celestials.world

import io.github.seggan.galactipylon.GalactipylonRegistry
import io.github.seggan.galactipylon.datagen.DimensionData
import org.bukkit.NamespacedKey
import org.bukkit.World

abstract class AlienWorld(key: NamespacedKey) : CelestialWorld(key) {

    abstract val dimension: DimensionData


    final override fun loadWorld(): World {
        TODO("Not yet implemented")
    }

    override fun register() {
        super.register()
        GalactipylonRegistry.ALIEN_WORLDS.register(this)
    }
}