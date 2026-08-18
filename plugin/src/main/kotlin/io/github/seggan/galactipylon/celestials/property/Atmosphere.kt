package io.github.seggan.galactipylon.celestials.property

import io.github.pylonmc.rebar.fluid.RebarFluid
import kotlin.math.abs

data class Atmosphere(
    /**
     * In standard atmospheres
     */
    val surfacePressure: Double,
    /**
     * In percent
     */
    val composition: Map<RebarFluid, Double>
) {
    init {
        require(abs(composition.values.sum() - 100) < 1e-6) { "Atmosphere composition must sum to 100%" }
    }
}