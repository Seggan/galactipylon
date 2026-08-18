package io.github.seggan.galactipylon.base.majuscule

import dev.wyck.biome.Biome
import dev.wyck.biome.BiomeSpecialEffects
import dev.wyck.biome.ClimateSettings
import dev.wyck.environment.attribute.EnvironmentAttributes
import dev.wyck.level.dimension.Dimension
import dev.wyck.level.dimension.Skybox
import dev.wyck.level.dimension.clock.WorldClock
import dev.wyck.level.dimension.timeline.AttributeTrack
import dev.wyck.level.dimension.timeline.Easing
import dev.wyck.level.dimension.timeline.Timeline
import dev.wyck.worldgen.biome.BiomeSource
import dev.wyck.worldgen.chunk.ChunkGenerator
import dev.wyck.worldgen.climate.ClimateParameter
import dev.wyck.worldgen.climate.ClimatePoint
import dev.wyck.worldgen.function.DensityFunction
import dev.wyck.worldgen.heightproviders.VerticalAnchor
import dev.wyck.worldgen.noise.Noise
import dev.wyck.worldgen.noise.NoiseRouter
import dev.wyck.worldgen.noise.NoiseSettings
import dev.wyck.worldgen.surface.SurfaceRule
import io.github.seggan.galactipylon.GalacticFluid
import io.github.seggan.galactipylon.asResourceKey
import io.github.seggan.galactipylon.base.Sun
import io.github.seggan.galactipylon.celestials.property.Atmosphere
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.AlienWorld
import io.github.seggan.galactipylon.galacticKey
import org.bukkit.Color
import org.bukkit.Material
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object Majuscule : AlienWorld(galacticKey("majuscule")) {

    override val orbit = Orbit(
        parent = Sun,
        semimajorAxis = 3.86e11,
        eccentricity = 0.003,
        longitudeOfPeriapsis = 1.32,
        timeOfPeriapsis = Instant.parse("2000-01-01T00:00:00Z") + 395507.7.days
    )

    override val displayMaterial = Material.WHITE_WOOL

    override val mass = 4.6e25

    override val gravity = 1.89

    override val atmosphere = Atmosphere(
        78.5,
        mapOf(
            GalacticFluid.WATER_VAPOR to 95.0,
            GalacticFluid.CARBON_DIOXIDE to 4.5,
            GalacticFluid.HYDROGEN to 0.5
        )
    )

    private val skyColor = Color.fromRGB(0xf2ffff)

    private val clock = WorldClock.of(key.asResourceKey())

    override val dimension = Dimension.builder(key.asResourceKey())
        .ambientLight(0f)
        .hasSkyLight(true)
        .defaultClock(clock)
        .hasCeiling(false)
        .hasEnderDragonFight(false)
        .skybox(Skybox.NONE)
        .minY(MIN_HEIGHT)
        .height(TOTAL_HEIGHT)
        .logicalHeight(TOTAL_HEIGHT)
        .attribute(EnvironmentAttributes.MONSTERS_BURN, false)
        .attribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true)
        .attribute(EnvironmentAttributes.FOG_END_DISTANCE, 50f)
        .timeline(
            Timeline.builder()
                .key(key.asResourceKey())
                .clock(clock)
                .periodTicks(DAY_LENGTH_TICKS)
                .track(
                    AttributeTrack.builder<Float>()
                        .attribute(EnvironmentAttributes.SKY_LIGHT_FACTOR)
                        .easing(Easing.LINEAR)
                        .keyframe(DAY_LENGTH_TICKS / 24, 1f)
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24, 1f)
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, 0.1f)
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, 0.1f)
                        .build()
                )
                .track(
                    AttributeTrack.builder<Float>()
                        .attribute(EnvironmentAttributes.SKY_LIGHT_LEVEL)
                        .easing(Easing.LINEAR)
                        .keyframe(DAY_LENGTH_TICKS / 24, 9f)
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24, 9f)
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, 0f)
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, 0f)
                        .build()
                )
                .track(
                    AttributeTrack.builder<Int>()
                        .attribute(EnvironmentAttributes.SKY_COLOR)
                        .easing(Easing.LINEAR)
                        .keyframe(DAY_LENGTH_TICKS / 24, skyColor.asRGB())
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24, skyColor.asRGB())
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, Color.BLACK.asRGB())
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, Color.BLACK.asRGB())
                        .build()
                )
                .track(
                    AttributeTrack.builder<Int>()
                        .attribute(EnvironmentAttributes.FOG_COLOR)
                        .easing(Easing.LINEAR)
                        .keyframe(DAY_LENGTH_TICKS / 24, skyColor.asRGB())
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24, skyColor.asRGB())
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, Color.BLACK.asRGB())
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, Color.BLACK.asRGB())
                        .build()
                )
                .timeMarker(galacticKey("majuscule_morning").asResourceKey(), 0, true)
                .timeMarker(galacticKey("majuscule_noon").asResourceKey(), DAY_LENGTH_TICKS / 4, true)
                .timeMarker(galacticKey("majuscule_evening").asResourceKey(), DAY_LENGTH_TICKS / 2, true)
                .timeMarker(galacticKey("majuscule_midnight").asResourceKey(), DAY_LENGTH_TICKS / 4 * 3, true)
                .register()
        )
        .register()

    private val epipelagic = Biome.builder(galacticKey("majuscule_epipelagic").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, skyColor.asRGB())
        .attribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x0007cc)
        .attribute(EnvironmentAttributes.SKY_COLOR, skyColor.asRGB())
        .attribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 50f)
        .climateSettings(
            ClimateSettings.builder()
                .hasPrecipitation(false)
                .temperature(-0.5f)
                .downfall(0f)
                .build()
        )
        .specialEffects(
            BiomeSpecialEffects.DEFAULT.toBuilder()
                .waterColor(0x0007cc)
                .build()
        )
        .register()

    private val mesopelagic = Biome.builder(galacticKey("majuscule_mesopelagic").asResourceKey())
        .attribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x000575)
        .attribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 25f)
        .climateSettings(
            ClimateSettings.builder()
                .hasPrecipitation(false)
                .temperature(-0.5f)
                .downfall(0f)
                .build()
        )
        .specialEffects(
            BiomeSpecialEffects.DEFAULT.toBuilder()
                .waterColor(0x000575)
                .build()
        )
        .register()

    private val bathypelagic = Biome.builder(galacticKey("majuscule_bathypelagic").asResourceKey())
        .attribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x000224)
        .attribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 12f)
        .climateSettings(
            ClimateSettings.builder()
                .hasPrecipitation(false)
                .temperature(-0.5f)
                .downfall(0f)
                .build()
        )
        .specialEffects(
            BiomeSpecialEffects.DEFAULT.toBuilder()
                .waterColor(0x000224)
                .build()
        )
        .register()

    override val generator = ChunkGenerator.noise()
        .biomeSource(
            BiomeSource.multiNoise()
                .add(
                    epipelagic, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.zero())
                        .weirdness(ClimateParameter.zero())
                        .depth(ClimateParameter.point(120.0 / TOTAL_HEIGHT))
                        .continentalness(ClimateParameter.zero())
                        .build()
                )
                .add(
                    mesopelagic, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.zero())
                        .weirdness(ClimateParameter.zero())
                        .depth(ClimateParameter.point(64.0 / TOTAL_HEIGHT))
                        .continentalness(ClimateParameter.zero())
                        .build()
                )
                .add(
                    bathypelagic, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.zero())
                        .weirdness(ClimateParameter.zero())
                        .depth(ClimateParameter.point(0.0 / TOTAL_HEIGHT))
                        .continentalness(ClimateParameter.zero())
                        .build()
                )
                .build()
        )
        .noise(
            Noise.builder()
                .seaLevel(120)
                .oreVeinsEnabled(false)
                .aquifersEnabled(false)
                .defaultBlock(Material.WATER)
                .defaultFluid(Material.AIR)
                .noiseSettings(NoiseSettings.of(MIN_HEIGHT, TOTAL_HEIGHT, 1, 2))
                .noiseRouter(
                    NoiseRouter.builder()
                        .barrier(DensityFunction.zero())
                        .fluidLevelFloodedness(DensityFunction.constant(-1.0))
                        .fluidLevelSpread(DensityFunction.zero())
                        .lava(DensityFunction.zero())

                        .temperature(DensityFunction.zero())
                        .vegetation(DensityFunction.zero())
                        .continents(DensityFunction.zero())
                        .erosion(DensityFunction.zero())
                        .depth(DensityFunction.yClampedGradient(MIN_HEIGHT, 120, MIN_HEIGHT.toDouble() / TOTAL_HEIGHT, 120.0 / TOTAL_HEIGHT))
                        .ridges(DensityFunction.zero())

                        .veinToggle(DensityFunction.constant(-1.0))
                        .veinRidged(DensityFunction.constant(0.0))
                        .veinGap(DensityFunction.constant(0.0))

                        .preliminarySurfaceLevel(DensityFunction.constant(-100.0))
                        .finalDensity(
                            DensityFunction.yClampedGradient(MIN_HEIGHT, 127, 1.0, 0.0)
                        )

                        .build()
                )
                .surfaceRule(SurfaceRule.ifTrue(
                    SurfaceRule.yBlockCheck(VerticalAnchor.top(), 0),
                    SurfaceRule.block(Material.AIR) // basically just a nop surface rule
                ))
                .build()
        )
        .build()

    private const val DAY_LENGTH_TICKS = 48000
    private const val MIN_HEIGHT = -64
    private const val MAX_HEIGHT = 320
    private const val TOTAL_HEIGHT = MIN_HEIGHT + MAX_HEIGHT
}