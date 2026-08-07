package io.github.seggan.galactipylon.celestials.property

import io.github.seggan.galactipylon.TAU
import io.github.seggan.galactipylon.celestials.CelestialObject
import org.joml.Vector2d
import kotlin.math.*
import kotlin.time.Instant

data class Orbit(
    val parent: CelestialObject,
    val semimajorAxis: Double, // a
    val eccentricity: Double, // e
    // Our orbits are always flat, so inclination is always 0
    // Longitude of the ascending node is also always 0
    val longitudeOfPeriapsis: Double, // ϖ
    val timeOfPeriapsis: Instant // T
) {
    val period by lazy {
        TAU * sqrt((semimajorAxis * semimajorAxis * semimajorAxis) / (GRAVITATIONAL_CONSTANT * parent.mass))
    }

    val meanMotion by lazy { TAU / period }

    private fun meanAnomaly(time: Instant): Double {
        return meanMotion * (time - timeOfPeriapsis).inWholeSeconds
    }

    // code stolen from https://github.com/LordIdra/rust-kepler-solver/blob/master/src/ellipse.rs (thank you idra)
    private fun eccentricAnomaly(meanAnomaly: Double): Double {
        var eccentricAnomaly = meanAnomaly +
                (0.999999 * 4.0 * eccentricity * meanAnomaly * (PI - meanAnomaly)) /
                (8.0 * eccentricity * meanAnomaly + 4.0 * eccentricity * (eccentricity - PI) + PI * PI)

        // Iteration using Laguerre method
        while (true) {
            val sinEccentricAnomaly = sin(eccentricAnomaly)
            val cosEccentricAnomaly = cos(eccentricAnomaly)

            val f = meanAnomaly - eccentricAnomaly + eccentricity * sinEccentricAnomaly
            val fPrime = -1.0 + eccentricity * cosEccentricAnomaly
            val fPrimePrime = -eccentricity * sinEccentricAnomaly
            val delta = laguerreDelta(f, fPrime, fPrimePrime)
            if (abs(delta) < 1e-10) {
                break
            }

            eccentricAnomaly += delta
        }

        return eccentricAnomaly
    }

    private val beta by lazy {
        eccentricity / (1 + sqrt(1 - eccentricity * eccentricity))
    }

    private fun trueAnomaly(time: Instant): Double {
        val meanAnomaly = meanAnomaly(time) % TAU
        val eccentricAnomaly = eccentricAnomaly(meanAnomaly)

        val y = beta * sin(eccentricAnomaly)
        val x = 1 - beta * cos(eccentricAnomaly)
        return eccentricAnomaly + 2 * atan2(y, x)
    }

    fun position(time: Instant): Vector2d {
        val trueAnomaly = trueAnomaly(time)
        val r = (semimajorAxis * (1 - eccentricity * eccentricity)) / (1 + eccentricity * cos(trueAnomaly))
        val theta = longitudeOfPeriapsis + trueAnomaly
        return Vector2d(
            r * cos(theta),
            r * sin(theta)
        )
    }
}

// code stolen from https://github.com/LordIdra/rust-kepler-solver/blob/master/src/ellipse.rs (thank you idra)
private fun laguerreDelta(f: Double, fPrime: Double, fPrimePrime: Double): Double {
    val n = 2.0
    val nMinusOne = n - 1
    val a = (nMinusOne * nMinusOne) * (fPrime * fPrime) - n * nMinusOne * f * fPrimePrime
    var b = sqrt(abs(a))
    b = abs(b) * sign(fPrime) // prevent catastrophic cancellation
    return -(n * f) / (fPrime + b)
}

const val GRAVITATIONAL_CONSTANT = 6.674e-11