package io.github.seggan.galactipylon.base.overworld

import io.github.seggan.galactipylon.AU
import io.github.seggan.galactipylon.DEGREES
import io.github.seggan.galactipylon.base.Sun
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.PlanetaryWorld
import io.github.seggan.galactipylon.pluginKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import kotlin.time.Instant

object Overworld : PlanetaryWorld(pluginKey("overworld")) {

    override val gravity = 1.0

    override fun loadWorld(): World = Bukkit.getWorlds().first()

    override val orbit = Orbit(
        parent = Sun,
        semimajorAxis = 1 * AU,
        eccentricity = 0.01671022,
        longitudeOfPeriapsis = 102.94719 * DEGREES,
        timeOfPeriapsis = Instant.parse("2000-01-03T05:17:00Z")
    )

    override val displayMaterial = Material.GRASS_BLOCK

    override val mass = 5.97e24
}