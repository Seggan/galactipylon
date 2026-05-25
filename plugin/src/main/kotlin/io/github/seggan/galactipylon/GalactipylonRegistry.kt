package io.github.seggan.galactipylon

import io.github.pylonmc.rebar.registry.RebarRegistry
import io.github.seggan.galactipylon.celestials.CelestialObject
import io.github.seggan.galactipylon.celestials.world.AlienWorld

object GalactipylonRegistry {

    val CELESTIAL_OBJECTS = RebarRegistry<CelestialObject>(key("celestial_object"))
    val ALIEN_WORLDS = RebarRegistry<AlienWorld>(key("alien_world"))
}