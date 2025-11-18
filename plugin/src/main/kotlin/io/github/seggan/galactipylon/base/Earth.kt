package io.github.seggan.galactipylon.base

import io.github.seggan.galactipylon.api.CelestialWorld
import io.github.seggan.galactipylon.key
import org.bukkit.Bukkit

object Earth : CelestialWorld(key("earth")) {
    override fun loadWorld() = Bukkit.getWorld("world")!!
}