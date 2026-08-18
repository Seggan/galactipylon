package io.github.seggan.galactipylon.base.overworld

import io.github.seggan.galactipylon.AU
import io.github.seggan.galactipylon.DEGREES
import io.github.seggan.galactipylon.GalacticFluid
import io.github.seggan.galactipylon.base.Sun
import io.github.seggan.galactipylon.celestials.property.Atmosphere
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.PlanetaryWorld
import io.github.seggan.galactipylon.galacticKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import kotlin.time.Instant

object Overworld : PlanetaryWorld(galacticKey("overworld")) {

    override val gravity = 1.0

    override val atmosphere = Atmosphere(
        1.0,
        mapOf(
            GalacticFluid.NITROGEN to 77.0,
            GalacticFluid.OXYGEN to 21.0,
            GalacticFluid.ARGON to 0.9,
            GalacticFluid.CARBON_DIOXIDE to 0.05,
            GalacticFluid.WATER_VAPOR to 1.05
        )
    )

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