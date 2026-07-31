package io.github.seggan.galactipylon.celestials.property

import io.github.seggan.galactipylon.celestials.CelestialObject
import kotlin.time.Instant

data class Orbit(
    val parent: CelestialObject,
    val semimajorAxis: Double, // a
    val eccentricity: Double, // e
    // Our orbits are always flat, so inclination is always 0
    // Longitude of the ascending node is also always 0
    val longitudeOfPeriapsis: Double, // ϖ
    val timeOfPeriapsis: Instant // T
)