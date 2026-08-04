package io.github.seggan.galactipylon.guide

import io.github.pylonmc.rebar.guide.button.PageButton
import io.github.seggan.galactipylon.celestials.CelestialObject
import io.github.seggan.galactipylon.celestials.world.PlanetaryWorld
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.invui.Click

class CelestialButton(val celestial: CelestialObject) : PageButton(celestial.displayItem, OrbitersPage(celestial)) {
    override fun handleClick(clickType: ClickType, player: Player, click: Click) {
        if (clickType == ClickType.RIGHT && celestial is PlanetaryWorld) {
            player.teleportAsync(Location(celestial.world, 0.0, 0.0, 0.0))
        } else {
            super.handleClick(clickType, player, click)
        }
    }
}