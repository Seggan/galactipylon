package io.github.seggan.galactipylon.celestials.world

import io.github.seggan.galactipylon.GalactipylonRegistry
import io.github.seggan.galactipylon.celestials.PlanetaryObject
import io.github.seggan.galactipylon.pluginKey
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

abstract class PlanetaryWorld(key: NamespacedKey) : PlanetaryObject(key), Listener {

    val worldName = "gp_planet_${key.key}"

    val world by lazy { loadWorld() }

    abstract val gravity: Double

    protected abstract fun loadWorld(): World

    companion object : Listener {

        private val gravityKey = pluginKey("gravity")

        @EventHandler
        private fun playerChangeWorld(e: PlayerTeleportEvent) {
            val player = e.player
            for (obj in GalactipylonRegistry.CELESTIAL_OBJECTS) {
                if (obj !is PlanetaryWorld) continue
                if (e.from.world.uid == obj.world.uid) {
                    player.getAttribute(Attribute.GRAVITY)?.removeModifier(gravityKey)
                }
                if (e.to.world.uid == obj.world.uid) {
                    player.getAttribute(Attribute.GRAVITY)?.addModifier(
                        AttributeModifier(
                            gravityKey,
                            obj.gravity - 1,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                        )
                    )
                }
            }
        }
    }
}