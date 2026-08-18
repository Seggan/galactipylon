package io.github.seggan.galactipylon.celestials.world

import io.github.seggan.galactipylon.GalacticRegistry
import io.github.seggan.galactipylon.celestials.PlanetaryObject
import io.github.seggan.galactipylon.celestials.property.Atmosphere
import io.github.seggan.galactipylon.galacticKey
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent
import kotlin.math.pow

abstract class PlanetaryWorld(key: NamespacedKey) : PlanetaryObject(key), Listener {

    val worldName = "gp_planet_${key.key}"

    val world by lazy { loadWorld() }

    /**
     * In standard gravities
     */
    abstract val gravity: Double

    open val atmosphere: Atmosphere? = null

    protected abstract fun loadWorld(): World

    val gravityModifier by lazy {
        AttributeModifier(
            gravityKey,
            gravity - 1,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
    }

    val atmosphereModifier by lazy {
        val pressure = atmosphere?.surfacePressure ?: 0.0
        val modifier = (1 - PLAYER_FRICTION.pow(pressure)) / (1 - PLAYER_FRICTION) // invert vanilla calculation
        AttributeModifier(
            atmosphereKey,
            modifier - 1,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
    }

    companion object : Listener {

        private const val PLAYER_FRICTION = 0.98

        private val gravityKey = galacticKey("gravity")
        private val atmosphereKey = galacticKey("atmosphere")

        @EventHandler
        private fun playerChangeWorld(e: PlayerTeleportEvent) {
            val player = e.player
            for (obj in GalacticRegistry.CELESTIAL_OBJECTS) {
                if (obj !is PlanetaryWorld) continue
                if (e.from.world.uid == obj.world.uid) {
                    player.getAttribute(Attribute.GRAVITY)?.removeModifier(gravityKey)
                    player.getAttribute(Attribute.AIR_DRAG_MODIFIER)?.removeModifier(atmosphereKey)
                }
                if (e.to.world.uid == obj.world.uid) {
                    player.getAttribute(Attribute.GRAVITY)?.addModifier(obj.gravityModifier)
                    player.getAttribute(Attribute.AIR_DRAG_MODIFIER)?.addModifier(obj.atmosphereModifier)
                }
            }
        }

        @EventHandler
        private fun entityChangeWorld(e: EntityTeleportEvent) {
            val entity = e.entity as? LivingEntity ?: return
            for (obj in GalacticRegistry.CELESTIAL_OBJECTS) {
                if (obj !is PlanetaryWorld) continue
                if (e.from.world.uid == obj.world.uid) {
                    entity.getAttribute(Attribute.GRAVITY)?.removeModifier(gravityKey)
                    entity.getAttribute(Attribute.AIR_DRAG_MODIFIER)?.removeModifier(atmosphereKey)
                }
                if (e.to?.world?.uid == obj.world.uid) {
                    entity.getAttribute(Attribute.GRAVITY)?.addModifier(obj.gravityModifier)
                    entity.getAttribute(Attribute.AIR_DRAG_MODIFIER)?.addModifier(obj.atmosphereModifier)
                }
            }
        }

        @EventHandler
        private fun entitySpawned(e: EntitySpawnEvent) {
            val entity = e.entity as? LivingEntity ?: return
            val obj = GalacticRegistry.CELESTIAL_OBJECTS
                .find { it is PlanetaryWorld && it.world == entity.world } as? PlanetaryWorld? ?: return
            entity.getAttribute(Attribute.GRAVITY)?.addModifier(obj.gravityModifier)
            entity.getAttribute(Attribute.AIR_DRAG_MODIFIER)?.addModifier(obj.atmosphereModifier)
        }
    }
}