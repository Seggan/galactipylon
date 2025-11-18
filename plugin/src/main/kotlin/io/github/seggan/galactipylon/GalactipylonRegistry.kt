package io.github.seggan.galactipylon

import io.github.pylonmc.pylon.core.registry.PylonRegistry
import io.github.pylonmc.pylon.core.registry.PylonRegistryKey

object GalactipylonRegistry {

    val CELESTIAL_OBJECTS = PylonRegistry(PylonRegistryKey(key("celestial_object")))
}