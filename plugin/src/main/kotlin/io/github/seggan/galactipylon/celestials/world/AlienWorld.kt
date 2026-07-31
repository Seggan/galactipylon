package io.github.seggan.galactipylon.celestials.world

import io.github.seggan.galactipylon.GalactipylonRegistry
import org.bukkit.NamespacedKey
import org.bukkit.World

abstract class AlienWorld(key: NamespacedKey) : PlanetaryWorld(key) {

    final override fun loadWorld(): World {
        TODO("Not yet implemented")
    }

    override fun register() {
        super.register()
        GalactipylonRegistry.ALIEN_WORLDS.register(this)
    }
}