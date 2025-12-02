package io.github.seggan.galactipylon.celestials.property

import io.github.seggan.galactipylon.celestials.CelestialObject
import io.github.seggan.galactipylon.unit.Angle
import io.github.seggan.galactipylon.unit.Distance
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Orbit @OptIn(ExperimentalTime::class) constructor(
    val parent: CelestialObject,
    val semimajorAxis: Distance, // a
    val eccentricity: Double, // e
    // Our orbits are always flat, so inclination is always 0
    // Longitude of the ascending node is also always 0
    val longitudeOfPeriapsis: Angle, // ω + Ω
    val timeOfPeriapsis: Instant // T
)