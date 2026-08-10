package io.github.seggan.galactipylon.celestials.property

import io.github.seggan.galactipylon.TAU
import io.github.seggan.galactipylon.celestials.CelestialObject
import org.joml.Matrix2d
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

    fun trueAnomaly(time: Instant): Double {
        val meanAnomaly = meanAnomaly(time) % TAU
        val eccentricAnomaly = eccentricAnomaly(meanAnomaly)

        val y = beta * sin(eccentricAnomaly)
        val x = 1 - beta * cos(eccentricAnomaly)
        return eccentricAnomaly + 2 * atan2(y, x)
    }

    fun position(trueAnomaly: Double): Vector2d {
        val r = (semimajorAxis * (1 - eccentricity * eccentricity)) / (1 + eccentricity * cos(trueAnomaly))
        val theta = longitudeOfPeriapsis + trueAnomaly
        return Vector2d(
            r * cos(theta),
            r * sin(theta)
        )
    }

    private val rotationMatrix by lazy { Matrix2d().rotate(longitudeOfPeriapsis) }

    fun velocity(trueAnomaly: Double): Vector2d {
        val p = semimajorAxis * (1 - eccentricity * eccentricity)
        val r = p / (1 + eccentricity * cos(trueAnomaly))
        val factor = sqrt(GRAVITATIONAL_CONSTANT * parent.mass / p)
        return Vector2d(
            factor * -sin(trueAnomaly),
            factor * (eccentricity + cos(trueAnomaly))
        ).mul(rotationMatrix)
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

private const val M = 0
private const val EPSILON = 1e-7
private const val MAX_ITERATIONS = 100
private val LOG_2 = ln(2.0)

// based on https://www.esa.int/gsp/ACT/doc/MAD/pub/ACT-RPR-MAD-2014-RevisitingLambertProblem.pdf
fun solveLambert(
    rh1: Vector2d,
    rh2: Vector2d,
    flightTime: Double,
    gravitationalParameter: Double
): Pair<Vector2d, Vector2d> {
    @Suppress("SimplifyBooleanWithConstants")
    check(M == 0) { "Code written for M = 0, please update you doofus" }

    val c = rh1.distance(rh2)
    val r1 = rh1.length()
    val r2 = rh2.length()
    val s = (r1 + r2 + c) / 2

    var lambda = sqrt(1 - c / s)
    val crossZ = rh1.x * rh2.y - rh1.y * rh2.x
    if (crossZ < 0) {
        lambda = -lambda
    }

    val targetT = sqrt((2 * gravitationalParameter) / (s * s * s)) * flightTime // T

    val l2 = lambda * lambda
    val l3 = l2 * lambda
    val l5 = l3 * l2
    val oml2 = 1 - l2

    // Compute initial guess
    val t0 = acos(lambda) + lambda * sqrt(oml2) + M * PI // eq (19)
    val t1 = (2.0 / 3.0) * (1 - l3) // eq (21)
    val initialGuess = if (targetT >= t0) {
        (t0 / targetT).pow(2.0 / 3.0) - 1
    } else if (targetT < t1) {
        (5.0 / 2.0) * ((t1 * (t1 - targetT)) / (targetT * (1 - l5))) + 1
    } else {
        (t0 / targetT).pow(LOG_2 / ln(t1 / t0)) - 1
    }

    var found = false
    var x = initialGuess
    for (_ in 0 until MAX_ITERATIONS) {
        val x2 = x * x
        val omx2 = 1 - x2

        val y = sqrt(1 - l2 * omx2)
        val y2 = y * y
        val y3 = y2 * y
        val y5 = y3 * y2
        val rtx = if (abs(x - 1) < 1e-4) {
            battinTof(x, lambda, y)
        } else {
            val psi = if (x > 1) {
                // hyperbolic orbit do do do do do
                acosh(x * y - lambda * (x2 - 1))
            } else {
                acos(x * y + lambda * omx2)
            }
            ((psi + M * PI) / sqrt(abs(omx2)) - x + lambda * y) / omx2
        }
        val d1tx = (3 * rtx * x - 2 + 2 * l3 * (x / y)) / omx2
        val d2tx = (3 * rtx + 5 * x * d1tx + 2 * oml2 * (l3 / y3)) / omx2
        val d3tx = (7 * x * d2tx + 8 * d1tx - 6 * oml2 * l5 * (x / y5)) / omx2
        val tx = rtx - targetT

        val d1tx2 = d1tx * d1tx
        val neum = tx * (d1tx2 - tx * d2tx / 2)
        val denom = d1tx * (d1tx2 - tx * d2tx) + d3tx * tx * tx / 6

        val newX = x - neum / denom
        if (abs(x - newX) < EPSILON) {
            x = newX
            found = true
            break
        }

        if (newX < -1) {
            throw ArithmeticException("Householder left domain")
        }

        x = newX
    }
    if (!found) {
        throw ArithmeticException("Could not find solution within $MAX_ITERATIONS iterations")
    }

    val y = sqrt(1 - l2 * (1 - x * x))
    val gamma = sqrt(gravitationalParameter * s / 2)
    val rho = (r1 - r2) / c
    val sigma = sqrt(1 - rho * rho)

    val vr1 = gamma * ((lambda * y - x) - rho * (lambda * y + x)) / r1
    val vr2 = -gamma * ((lambda * y - x) + rho * (lambda * y + x)) / r2
    val vt1 = gamma * sigma * (y + lambda * x) / r1
    val vt2 = gamma * sigma * (y + lambda * x) / r2

    val ir1 = rh1.div(r1, Vector2d())
    val ir2 = rh2.div(r2, Vector2d())
    val direction = if (crossZ >= 0) 1.0 else -1.0
    val it1 = Vector2d(-ir1.y, ir1.x).mul(direction)
    val it2 = Vector2d(-ir2.y, ir2.x).mul(direction)

    val v1 = ir1.mul(vr1).add(it1.mul(vt1))
    val v2 = ir2.mul(vr2).add(it2.mul(vt2))

    return v1 to v2
}

private fun battinTof(
    x: Double,
    lambda: Double,
    y: Double
): Double {
    val eta = y - lambda * x
    val s1 = 0.5 * (1.0 - lambda - x * eta)

    var term = 1.0
    var sum = 1.0

    for (k in 0 until 20) {
        term *= (k + 3.0) / (k + 2.5) * s1
        sum += term

        if (abs(term) < 1e-14 * abs(sum)) {
            break
        }
    }

    val q = (4.0 / 3.0) * sum

    return 0.5 * (eta * eta * eta * q + 4.0 * lambda * eta)
}
