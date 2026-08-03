package io.github.seggan.galactipylon.base

import io.github.seggan.galactipylon.AU
import io.github.seggan.galactipylon.DEGREES
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.PlanetaryWorld
import io.github.seggan.galactipylon.key
import org.bukkit.Bukkit
import org.bukkit.Material
import kotlin.time.Instant

object Earth : PlanetaryWorld(key("earth")) {

    override fun loadWorld() = Bukkit.getWorld("world")!!

    override val orbit = Orbit(
        parent = Sun,
        semimajorAxis = 1 * AU,
        eccentricity = 0.01671022,
        longitudeOfPeriapsis = 102.94719 * DEGREES,
        timeOfPeriapsis = Instant.parse("2000-01-03T05:17:00Z")
    )

    override val displayMaterial = Material.GRASS_BLOCK
}