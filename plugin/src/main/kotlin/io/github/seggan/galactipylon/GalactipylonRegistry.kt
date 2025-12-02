package io.github.seggan.galactipylon

import io.github.pylonmc.pylon.core.registry.PylonRegistry
import io.github.pylonmc.pylon.core.registry.PylonRegistryKey
import io.github.seggan.galactipylon.celestials.world.AlienWorld

object GalactipylonRegistry {

    val CELESTIAL_OBJECTS = PylonRegistry(PylonRegistryKey(key("celestial_object")))
    val ALIEN_WORLDS = PylonRegistry(PylonRegistryKey<AlienWorld>(key("alien_world")))
}