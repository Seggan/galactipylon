package io.github.seggan.galactipylon.celestials.world

import io.github.pylonmc.rebar.async.BukkitMainThreadDispatcher
import io.github.seggan.galactipylon.GalacticRegistry
import io.github.seggan.galactipylon.Galactipylon
import io.github.seggan.galactipylon.celestials.PlanetaryObject
import io.github.seggan.galactipylon.celestials.property.Atmosphere
import io.github.seggan.galactipylon.nms.airDrag
import io.github.seggan.galactipylon.nms.gravity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

abstract class PlanetaryWorld(key: NamespacedKey) : PlanetaryObject(key), Listener {

    val worldName = "gp_planet_${key.key}"

    val world by lazy { loadWorld() }

    /**
     * In standard gravities
     */
    abstract val gravity: Double

    open val atmosphere: Atmosphere? = null

    protected abstract fun loadWorld(): World

    protected open fun getNewVelocity(entity: Entity, velocity: Vector): Vector {
        if (!entity.isInWater && !entity.hasNoPhysics()) {
            if (!(entity is Player && entity.isFlying)) {
                val entityGravity = entity.gravity
                velocity.y += entityGravity
                velocity.y -= entityGravity * gravity
            }

            velocity.multiply(1 / entity.airDrag)
            velocity.multiply(entity.airDrag.pow((atmosphere?.surfacePressure ?: 0.0).pow(0.7)))
        }
        return velocity
    }

    companion object : Listener {

        fun fromWorld(world: World): PlanetaryWorld? {
            return GalacticRegistry.CELESTIAL_OBJECTS.find { it is PlanetaryWorld && it.world == world } as? PlanetaryWorld
        }

        private val movementDispatcher = BukkitMainThreadDispatcher(Galactipylon, 1)
        private val movementScope = CoroutineScope(movementDispatcher + Job())

        private fun registerEntityForMovementModification(entity: Entity) {
            val startWorld = entity.world
            val obj = fromWorld(startWorld) ?: return
            movementScope.launch {
                while (true) {
                    delay(50.milliseconds)
                    if (!entity.isValid || entity.world != startWorld) break
                    val vel = entity.velocity
                    if (abs(vel.x) > 1e-6 || abs(vel.y) > 1e-6 || abs(vel.z) > 1e-6) {
                        entity.velocity = obj.getNewVelocity(entity, vel)
                    }
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        private fun playerChangeWorld(e: PlayerTeleportEvent) {
            val player = e.player
            if (e.from.world != e.to.world) {
                movementScope.launch {
                    delay(50.milliseconds)
                    registerEntityForMovementModification(player)
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        private fun entityChangeWorld(e: EntityTeleportEvent) {
            if (e.from.world != e.to?.world) {
                movementScope.launch {
                    delay(50.milliseconds)
                    registerEntityForMovementModification(e.entity)
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        private fun entitySpawned(e: EntitySpawnEvent) {
            registerEntityForMovementModification(e.entity)
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        private fun playerSpawn(e: PlayerJoinEvent) {
            registerEntityForMovementModification(e.player)
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        private fun entityLoaded(e: EntitiesLoadEvent) {
            e.entities.forEach(::registerEntityForMovementModification)
        }
    }
}