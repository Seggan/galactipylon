package io.github.seggan.galactipylon.celestials

import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.github.seggan.galactipylon.guide.CelestialButton
import io.github.seggan.galactipylon.guide.GalactipylonPages
import org.bukkit.NamespacedKey
import org.joml.Vector2d

abstract class StellarObject(key: NamespacedKey) : CelestialObject(key) {
    
    abstract val position: Vector2d

    final override val displayItem by lazy {
        ItemStackBuilder.gui(displayMaterial, key)
            .name(name)
            .build()
    }

    override fun register() {
        super.register()
        GalactipylonPages.UNIVERSE.addButton(CelestialButton(this))
    }
}