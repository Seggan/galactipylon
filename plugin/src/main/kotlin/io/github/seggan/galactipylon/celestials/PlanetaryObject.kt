package io.github.seggan.galactipylon.celestials

import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.github.seggan.galactipylon.celestials.property.Orbit
import org.bukkit.NamespacedKey

abstract class PlanetaryObject(key: NamespacedKey) : CelestialObject(key) {
    abstract val orbit: Orbit

    final override val displayItem by lazy {
        ItemStackBuilder.gui(displayMaterial, key)
            .name(name)
            .build()
    }
}