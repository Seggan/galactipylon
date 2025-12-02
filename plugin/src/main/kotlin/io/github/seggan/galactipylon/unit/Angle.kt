package io.github.seggan.galactipylon.unit

@JvmInline
value class Angle(val radians: Double) {

    inline val degrees: Double
        get() = radians * (180.0 / Math.PI)

    companion object {
        inline val Double.radians: Angle
            get() = Angle(this)

        inline val Double.degrees: Angle
            get() = Angle(this * (Math.PI / 180.0))
    }
}