package io.github.seggan.galactipylon.api

import org.bukkit.NamespacedKey
import org.bukkit.World

abstract class CelestialWorld(key: NamespacedKey) : CelestialObject(key) {
    val world by lazy { loadWorld() }

    protected abstract fun loadWorld(): World
}