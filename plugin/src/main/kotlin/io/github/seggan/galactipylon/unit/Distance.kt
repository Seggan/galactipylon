package io.github.seggan.galactipylon.unit

@JvmInline
value class Distance(val meters: Double) {
    inline val kilometers: Double
        get() = meters / 1_000

    inline val astronomicalUnits: Double
        get() = meters / 149_597_870_700

    inline val lightYears: Double
        get() = meters / 9_460_730_472_580_800

    companion object {
        inline val Double.meters: Distance
            get() = Distance(this)

        inline val Double.kilometers: Distance
            get() = Distance(this * 1_000)

        inline val Double.astronomicalUnits: Distance
            get() = Distance(this * 149_597_870_700)

        inline val Double.lightYears: Distance
            get() = Distance(this * 9_460_730_472_580_800)
    }
}