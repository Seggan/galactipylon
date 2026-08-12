package io.github.seggan.galactipylon.base

import dev.wyck.biome.Biome
import dev.wyck.biome.BiomeGenerationSettings
import dev.wyck.biome.BiomeSpecialEffects
import dev.wyck.biome.ClimateSettings
import dev.wyck.environment.attribute.EnvironmentAttributes
import dev.wyck.level.dimension.CardinalLightType
import dev.wyck.level.dimension.Dimension
import dev.wyck.level.dimension.Skybox
import dev.wyck.level.dimension.clock.WorldClock
import dev.wyck.level.dimension.timeline.AttributeTrack
import dev.wyck.level.dimension.timeline.Easing
import dev.wyck.level.dimension.timeline.Timeline
import dev.wyck.worldgen.Decoration
import dev.wyck.worldgen.HeightmapType
import dev.wyck.worldgen.biome.BiomeSource
import dev.wyck.worldgen.chunk.ChunkGenerator
import dev.wyck.worldgen.feature.ConfiguredFeature
import dev.wyck.worldgen.function.DensityFunction
import dev.wyck.worldgen.heightproviders.VerticalAnchor
import dev.wyck.worldgen.noise.Noise
import dev.wyck.worldgen.noise.NoiseRouter
import dev.wyck.worldgen.noise.NoiseSettings
import dev.wyck.worldgen.placement.PlacedFeature
import dev.wyck.worldgen.placement.PlacementModifier
import dev.wyck.worldgen.surface.SurfaceRule
import dev.wyck.worldgen.surface.condition.CaveSurface
import dev.wyck.worldgen.synth.NoiseParameters
import io.github.seggan.galactipylon.asResourceKey
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.AlienWorld
import io.github.seggan.galactipylon.key
import io.github.seggan.galactipylon.plus
import io.github.seggan.galactipylon.worldgen.feature.Crater
import org.bukkit.Color
import org.bukkit.Material
import kotlin.time.Instant

object Mars : AlienWorld(key("mars")) {

    override val orbit = Orbit(
        parent = Sun,
        semimajorAxis = 2.27936637e11,
        eccentricity = 0.09341233,
        longitudeOfPeriapsis = 5.8650191,
        timeOfPeriapsis = Instant.parse("2022-06-21T14:01:00Z")
    )

    override val displayMaterial = Material.RED_SANDSTONE

    override val mass = 6.417e23

    private val skyColor = Color.fromRGB(0xA1725F)
    private val sunsetColor = Color.fromRGB(0x8CA6BF)
    
    private val clock = WorldClock.of(key.asResourceKey())

    override val dimension = Dimension.builder(key.asResourceKey())
        .ambientLight(0.1f)
        .hasSkyLight(true)
        .defaultClock(clock)
        .hasCeiling(false)
        .hasEnderDragonFight(false)
        .skybox(Skybox.OVERWORLD)
        .minY(MIN_HEIGHT)
        .height(MIN_HEIGHT + MAX_HEIGHT)
        .logicalHeight(MIN_HEIGHT + MAX_HEIGHT)
        .cardinalLightType(CardinalLightType.DEFAULT)
        .attribute(EnvironmentAttributes.MOON_ANGLE, 180f)
        .attribute(EnvironmentAttributes.MONSTERS_BURN, false)
        .attribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true)
        .timeline(
            Timeline.builder()
                .key(key.asResourceKey())
                .clock(clock)
                .periodTicks(DAY_LENGTH_TICKS)
                .track(
                    AttributeTrack.builder<Float>()
                        .attribute(EnvironmentAttributes.SUN_ANGLE)
                        .easing(Easing.LINEAR)
                        .keyframe(0, 0f - 90f)
                        .keyframe(DAY_LENGTH_TICKS / 2, 180f - 90f)
                        .keyframe(DAY_LENGTH_TICKS, 360f - 90f)
                        .build()
                )
                .track(
                    AttributeTrack.builder<Float>()
                        .attribute(EnvironmentAttributes.STAR_ANGLE)
                        .easing(Easing.LINEAR)
                        .keyframe(0, 0f)
                        .keyframe(DAY_LENGTH_TICKS / 2, 180f)
                        .keyframe(DAY_LENGTH_TICKS, 360f)
                        .build()
                )
                .track(
                    AttributeTrack.builder<Float>()
                        .attribute(EnvironmentAttributes.STAR_BRIGHTNESS)
                        .easing(Easing.LINEAR)
                        .keyframe(0, 0f)
                        .keyframe(DAY_LENGTH_TICKS / 2, 0f)
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, 0.8f)
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, 0.8f)
                        .build()
                )
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
                .track(
                    AttributeTrack.builder<Int>()
                        .attribute(EnvironmentAttributes.SUNRISE_SUNSET_COLOR)
                        .easing(Easing.LINEAR)
                        .keyframe(0, sunsetColor.asARGB())
                        .keyframe(DAY_LENGTH_TICKS / 24 * 2, 0)
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24 * 2, 0)
                        .keyframe(DAY_LENGTH_TICKS / 2, sunsetColor.asARGB())
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24 * 2, 0)
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24 * 2, 0)
                        .build()
                )
                .timeMarker(key("mars_morning").asResourceKey(), 0, true)
                .timeMarker(key("mars_noon").asResourceKey(), DAY_LENGTH_TICKS / 4, true)
                .timeMarker(key("mars_evening").asResourceKey(), DAY_LENGTH_TICKS / 2, true)
                .timeMarker(key("mars_midnight").asResourceKey(), DAY_LENGTH_TICKS / 4 * 3, true)
                .register()
        )
        .register()

    private val marsHighlands = Biome.builder(key("mars_highlands").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, skyColor.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, skyColor.asRGB())
        .climateSettings(
            ClimateSettings.builder()
                .hasPrecipitation(false)
                .temperature(-0.5f)
                .downfall(0f)
                .build()
        )
        .specialEffects(BiomeSpecialEffects.DEFAULT)
        .generationSettings(
            BiomeGenerationSettings.builder()
                .feature(
                    Decoration.LOCAL_MODIFICATIONS, PlacedFeature.builder()
                        .feature(
                            ConfiguredFeature.custom<Crater.Config>()
                                .resourceKey(Crater.key())
                                .feature(Crater)
                                .config(Crater.Config(5..10, 10..25))
                                .build()
                        )
                        .modifier(
                            PlacementModifier.biomeFilter(),
                            PlacementModifier.rarityFilter(10),
                            PlacementModifier.inSquare(),
                            PlacementModifier.heightmap(HeightmapType.WORLD_SURFACE)
                        )
                        .build()
                )
                .build()
        )
        .register()

    private val mainNoise = NoiseParameters.builder()
        .resourceKey(key.asResourceKey())
        .firstOctave(-9)
        .amplitudes(1.0, 2.0, 2.0, 2.0, 1.5)
        .build()
        .register()

    override val generator = ChunkGenerator.noise()
        .biomeSource(
            BiomeSource.fixed(marsHighlands)
        )
        .noise(
            Noise.builder()
                .seaLevel(0)
                .oreVeinsEnabled(true)
                .aquifersEnabled(false)
                .defaultBlock(Material.RED_SANDSTONE)
                .defaultFluid(Material.AIR)
                .noiseSettings(NoiseSettings.of(MIN_HEIGHT, MIN_HEIGHT + MAX_HEIGHT, 1, 2))
                .noiseRouter(
                    NoiseRouter.builder()
                        .barrier(DensityFunction.zero())
                        .fluidLevelFloodedness(DensityFunction.constant(-1.0))
                        .fluidLevelSpread(DensityFunction.zero())
                        .lava(DensityFunction.zero())

                        .temperature(DensityFunction.zero())
                        .vegetation(DensityFunction.zero())
                        .continents(DensityFunction.noise(mainNoise, 1.0, 0.0).flatCache().clamp(-1.0, 1.0))
                        .erosion(DensityFunction.zero())
                        .depth(DensityFunction.zero())
                        .ridges(DensityFunction.zero())

                        .veinToggle(DensityFunction.constant(-1.0))
                        .veinRidged(DensityFunction.constant(0.0))
                        .veinGap(DensityFunction.constant(0.0))

                        .preliminarySurfaceLevel(DensityFunction.constant(-100.0))
                        .finalDensity(
                            DensityFunction.yClampedGradient(MIN_HEIGHT, 127, 1.0, -1.0)
                                    + DensityFunction.noise(mainNoise, 1.0, 0.0).flatCache().interpolated()
                                .halfNegative()
                                .squeeze()
                        )

                        .build()
                )
                .surfaceRule(
                    SurfaceRule.sequence(
                        SurfaceRule.ifTrue(
                            SurfaceRule.verticalGradient()
                                .randomName("bedrock")
                                .falseAtAndAbove(VerticalAnchor.aboveBottom(5))
                                .trueAtAndBelow(VerticalAnchor.aboveBottom(0))
                                .build(),
                            SurfaceRule.block(Material.BEDROCK)
                        ),
                        SurfaceRule.ifTrue(
                            SurfaceRule.stoneDepth(2, false, CaveSurface.FLOOR),
                            SurfaceRule.block(Material.RED_SAND)
                        )
                    )
                )
                .build()
        )
        .build()

    private const val DAY_LENGTH_TICKS = 24660
    private const val MIN_HEIGHT = -64
    private const val MAX_HEIGHT = 320
}