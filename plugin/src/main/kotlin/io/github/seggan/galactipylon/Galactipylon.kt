package io.github.seggan.galactipylon

import io.github.pylonmc.rebar.addon.RebarAddon
import io.github.pylonmc.rebar.content.guide.RebarGuide
import io.github.seggan.galactipylon.base.Sun
import io.github.seggan.galactipylon.base.majuscule.Majuscule
import io.github.seggan.galactipylon.base.overworld.Moon
import io.github.seggan.galactipylon.base.overworld.Overworld
import io.github.seggan.galactipylon.celestials.world.PlanetaryWorld
import io.github.seggan.galactipylon.guide.GalactipylonPages
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object Galactipylon : JavaPlugin(), RebarAddon {

    override fun onEnable() {
        registerWithRebar()
        Sun.register()
        Overworld.register()
        Moon.register()
        Majuscule.register()

        val pm = server.pluginManager

        pm.registerEvents(PlanetaryWorld, this)

        RebarGuide.rootPage.addPage(Material.END_STONE, GalactipylonPages.UNIVERSE)
    }

    override val javaPlugin = this
    override val defaultLanguage = Locale.ENGLISH
    override val material = Material.END_STONE
}

@JvmSynthetic
internal fun pluginKey(key: String) = NamespacedKey(Galactipylon, key)
