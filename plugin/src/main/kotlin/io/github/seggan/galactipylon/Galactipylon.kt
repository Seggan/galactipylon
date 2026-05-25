package io.github.seggan.galactipylon

import io.github.pylonmc.rebar.addon.RebarAddon
import io.github.seggan.galactipylon.base.Earth
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object Galactipylon : JavaPlugin(), RebarAddon {

    override fun onEnable() {
        registerWithRebar()
        Earth.register()
    }

    override val javaPlugin = this
    override val languages = setOf(Locale.ENGLISH)
    override val material = Material.END_STONE
}

@JvmSynthetic
internal fun key(key: String) = NamespacedKey(Galactipylon, key)
