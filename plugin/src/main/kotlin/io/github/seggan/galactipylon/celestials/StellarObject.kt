package io.github.seggan.galactipylon.celestials

import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.github.pylonmc.rebar.registry.RebarRegistry
import io.github.pylonmc.rebar.registry.RegistryHandler
import io.github.seggan.galactipylon.guide.CelestialButton
import io.github.seggan.galactipylon.guide.GalactipylonPages
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.MustBeInvokedByOverriders
import org.joml.Vector2d

abstract class StellarObject(key: NamespacedKey) : CelestialObject(key), RegistryHandler {
    
    abstract val position: Vector2d

    final override val displayItem by lazy {
        ItemStackBuilder.gui(displayMaterial, key)
            .name(name)
            .build()
    }

    @MustBeInvokedByOverriders
    override fun onRegister(registry: RebarRegistry<*>) {
        GalactipylonPages.UNIVERSE.addButton(CelestialButton(this))
    }
}