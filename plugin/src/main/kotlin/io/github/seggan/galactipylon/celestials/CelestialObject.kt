package io.github.seggan.galactipylon.celestials

import io.github.seggan.galactipylon.GalactipylonRegistry
import net.kyori.adventure.text.Component
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

sealed class CelestialObject(private val key: NamespacedKey) : Keyed {

    abstract val mass: Double

    val orbiters: List<PlanetaryObject>
        get() = GalactipylonRegistry.CELESTIAL_OBJECTS
            .filterIsInstance<PlanetaryObject>()
            .filter { it.orbit.parent == this }

    protected abstract val displayMaterial: Material

    open val name: Component = Component.translatable("${key.namespace}.planet.${key.key}")

    abstract val displayItem: ItemStack

    override fun getKey() = key

    fun register() {
        GalactipylonRegistry.CELESTIAL_OBJECTS.register(this)
    }
}