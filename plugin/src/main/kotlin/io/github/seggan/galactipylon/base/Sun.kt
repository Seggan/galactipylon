package io.github.seggan.galactipylon.base

import io.github.seggan.galactipylon.celestials.StellarObject
import io.github.seggan.galactipylon.key
import org.joml.Vector2i

object Sun : StellarObject(key("sun")) {
    override val position = Vector2i(0, 0)
}