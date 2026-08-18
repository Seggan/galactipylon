package io.github.seggan.galactipylon

import io.github.pylonmc.rebar.fluid.RebarFluid
import io.github.pylonmc.rebar.fluid.tags.FluidTemperature
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

object GalacticFluid {

    val NITROGEN = RebarFluid(
        galacticKey("nitrogen"),
        NamedTextColor.BLUE,
        Material.BLUE_STAINED_GLASS
    ).addTag(FluidTemperature.NORMAL).also(RebarFluid::register)

    val OXYGEN = RebarFluid(
        galacticKey("oxygen"),
        NamedTextColor.RED,
        Material.RED_STAINED_GLASS
    ).addTag(FluidTemperature.NORMAL).also(RebarFluid::register)

    val ARGON = RebarFluid(
        galacticKey("argon"),
        NamedTextColor.AQUA,
        Material.BLUE_STAINED_GLASS
    ).addTag(FluidTemperature.NORMAL).also(RebarFluid::register)

    val WATER_VAPOR = RebarFluid(
        galacticKey("water_vapor"),
        NamedTextColor.BLUE,
        Material.BLUE_STAINED_GLASS
    ).addTag(FluidTemperature.HOT).also(RebarFluid::register)

    val CARBON_DIOXIDE = RebarFluid(
        galacticKey("carbon_dioxide"),
        NamedTextColor.GRAY,
        Material.LIGHT_GRAY_STAINED_GLASS
    ).addTag(FluidTemperature.NORMAL).also(RebarFluid::register)

    val HYDROGEN = RebarFluid(
        galacticKey("hydrogen"),
        NamedTextColor.WHITE,
        Material.WHITE_STAINED_GLASS
    ).addTag(FluidTemperature.NORMAL).also(RebarFluid::register)
}