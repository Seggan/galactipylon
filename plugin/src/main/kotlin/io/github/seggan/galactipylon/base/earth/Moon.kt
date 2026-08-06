package io.github.seggan.galactipylon.base.earth

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
import dev.wyck.worldgen.blockpredicates.BlockPredicate
import dev.wyck.worldgen.carver.CanyonCarverConfiguration
import dev.wyck.worldgen.carver.CarverDebugSettings
import dev.wyck.worldgen.carver.ConfiguredWorldCarver
import dev.wyck.worldgen.chunk.ChunkGenerator
import dev.wyck.worldgen.climate.ClimateParameter
import dev.wyck.worldgen.climate.ClimatePoint
import dev.wyck.worldgen.feature.ConfiguredFeature
import dev.wyck.worldgen.feature.FeatureType
import dev.wyck.worldgen.feature.configurations.FeatureConfiguration
import dev.wyck.worldgen.function.DensityFunction
import dev.wyck.worldgen.heightproviders.HeightProvider
import dev.wyck.worldgen.heightproviders.VerticalAnchor
import dev.wyck.worldgen.noise.Noise
import dev.wyck.worldgen.noise.NoiseRouter
import dev.wyck.worldgen.noise.NoiseSettings
import dev.wyck.worldgen.placement.PlacedFeature
import dev.wyck.worldgen.placement.PlacementModifier
import dev.wyck.worldgen.surface.SurfaceRule
import dev.wyck.worldgen.surface.condition.CaveSurface
import dev.wyck.worldgen.synth.NoiseParameters
import dev.wyck.worldgen.valueproviders.FloatProvider
import io.github.seggan.galactipylon.*
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.AlienWorld
import io.github.seggan.galactipylon.worldgen.feature.Crater
import org.bukkit.Color
import org.bukkit.Material
import kotlin.time.Instant

object Moon : AlienWorld(key("moon")) {

    override val orbit = Orbit(
        parent = Earth,
        semimajorAxis = 3.844e8,
        eccentricity = 0.0554,
        longitudeOfPeriapsis = 83 * DEGREES,
        timeOfPeriapsis = Instant.parse("1999-12-17T18:14:00Z")
    )

    override val displayMaterial = Material.END_STONE

    override val dimension = Dimension.builder(key.asResourceKey())
        .ambientLight(0.1f)
        .hasSkyLight(true)
        .defaultClock(WorldClock.OVERWORLD)
        .hasCeiling(false)
        .hasEnderDragonFight(false)
        .skybox(Skybox.OVERWORLD)
        .minY(MIN_HEIGHT)
        .height(MIN_HEIGHT + MAX_HEIGHT)
        .logicalHeight(MIN_HEIGHT + MAX_HEIGHT)
        .cardinalLightType(CardinalLightType.DEFAULT)
        .attribute(EnvironmentAttributes.FOG_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.MOON_ANGLE, 180f)
        .attribute(EnvironmentAttributes.STAR_ANGLE, 90f)
        .attribute(EnvironmentAttributes.SKY_LIGHT_LEVEL, 0f)
        .attribute(EnvironmentAttributes.MONSTERS_BURN, false)
        .attribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true)
        .timeline(
            Timeline.builder()
                .key(key.asResourceKey())
                .clock(WorldClock.OVERWORLD)
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
                        .attribute(EnvironmentAttributes.STAR_BRIGHTNESS)
                        .easing(Easing.LINEAR)
                        .keyframe(DAY_LENGTH_TICKS / 24, 0.1f)
                        .keyframe(DAY_LENGTH_TICKS / 2 - DAY_LENGTH_TICKS / 24, 0.1f)
                        .keyframe(DAY_LENGTH_TICKS / 2 + DAY_LENGTH_TICKS / 24, 1f)
                        .keyframe(DAY_LENGTH_TICKS - DAY_LENGTH_TICKS / 24, 1f)
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
                .timeMarker(key("lunar_morning").asResourceKey(), 0, true)
                .timeMarker(key("lunar_noon").asResourceKey(), DAY_LENGTH_TICKS / 4, true)
                .timeMarker(key("lunar_evening").asResourceKey(), DAY_LENGTH_TICKS / 2, true)
                .timeMarker(key("lunar_midnight").asResourceKey(), DAY_LENGTH_TICKS / 4 * 3, true)
                .register()
        )
        .register()

    private val lunarMaria = Biome.builder(key("lunar_maria").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, Color.BLACK.asRGB())
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
                                .config(Crater.Config(4..10, 10..30))
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

    private val lunarHighlands = Biome.builder(key("lunar_highlands").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, Color.BLACK.asRGB())
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
                                .config(Crater.Config(1..5, 2..8))
                                .build()
                        )
                        .modifier(
                            PlacementModifier.biomeFilter(),
                            PlacementModifier.rarityFilter(40),
                            PlacementModifier.inSquare(),
                            PlacementModifier.heightmap(HeightmapType.WORLD_SURFACE)
                        )
                        .build()
                )
                .build()
        )
        .register()

    private val lunarFarSide = Biome.builder(key("lunar_far_side").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, Color.BLACK.asRGB())
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
                                .config(Crater.Config(1..10, 2..25))
                                .build()
                        )
                        .modifier(
                            PlacementModifier.biomeFilter(),
                            PlacementModifier.inSquare(),
                            PlacementModifier.heightmap(HeightmapType.WORLD_SURFACE)
                        )
                        .build()
                )
                .build()
        )
        .register()

    private val impactBasin = Biome.builder(key("lunar_impact_basin").asResourceKey())
        .attribute(EnvironmentAttributes.FOG_COLOR, Color.BLACK.asRGB())
        .attribute(EnvironmentAttributes.SKY_COLOR, Color.BLACK.asRGB())
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
                                .config(Crater.Config(1..10, 2..25))
                                .build()
                        )
                        .modifier(
                            PlacementModifier.biomeFilter(),
                            PlacementModifier.rarityFilter(3),
                            PlacementModifier.inSquare(),
                            PlacementModifier.heightmap(HeightmapType.WORLD_SURFACE)
                        )
                        .build()
                )
                .feature(
                    Decoration.SURFACE_STRUCTURES, PlacedFeature.builder()
                        .feature(
                            ConfiguredFeature.of(
                                FeatureType.BLOCK_BLOB, FeatureConfiguration.blockBlob()
                                    .state(Material.BASALT)
                                    .canPlaceOn(BlockPredicate.alwaysTrue())
                                    .build()
                            )
                        )
                        .modifier(
                            PlacementModifier.biomeFilter(),
                            PlacementModifier.inSquare(),
                            PlacementModifier.heightmap(HeightmapType.WORLD_SURFACE)
                        )
                        .build()
                )
                .carver(
                    ConfiguredWorldCarver.canyon()
                        .config(
                            CanyonCarverConfiguration.builder()
                                .probability(0.05f)
                                .y(HeightProvider.uniform(VerticalAnchor.aboveBottom(40), VerticalAnchor.belowTop(100)))
                                .yScale(FloatProvider.constant(3f))
                                .verticalRotation(FloatProvider.uniform(0f, 0.1f))
                                .lavaLevel(VerticalAnchor.bottom())
                                .replaceable(Material.ANDESITE, Material.GRAY_CONCRETE_POWDER)
                                .debugSettings(CarverDebugSettings.DEFAULT)
                                .shape(
                                    CanyonCarverConfiguration.CanyonShapeConfiguration.builder()
                                        .distanceFactor(FloatProvider.uniform(0.75f, 1f))
                                        .thickness(FloatProvider.trapezoid(1f, 6f, 3f))
                                        .horizontalRadiusFactor(FloatProvider.uniform(0.75f, 1f))
                                        .verticalRadiusDefaultFactor(1f)
                                        .verticalRadiusCenterFactor(0f)
                                        .widthSmoothness(3)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .register()

    private val mainNoise = NoiseParameters.builder()
        .resourceKey(key.asResourceKey())
        .firstOctave(-10)
        .amplitudes(1.0, 2.0, 2.0, 2.0, 2.0, 1.0, 1.0)
        .build()
        .register()

    private val erosionNoise = NoiseParameters.builder()
        .resourceKey(key("lunar_erosion").asResourceKey())
        .firstOctave(-10)
        .amplitudes(2.0, 1.0, 0.0)
        .build()
        .register()

    private val impactBasinNoise = DensityFunction
        .noise(
            NoiseParameters.builder()
                .resourceKey(key("lunar_impact_basins").asResourceKey())
                .firstOctave(-9)
                .amplitudes(1.0)
                .build()
                .register(),
            0.1,
            0.0
        )
        .square()
        .clamp(0.5, 100.0)
        .add(DensityFunction.constant(-0.5))
        .mul(DensityFunction.constant(3.0))
        .squeeze()

    override val generator = ChunkGenerator.noise()
        .biomeSource(
            BiomeSource.multiNoise()
                .add(
                    lunarHighlands, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.zero())
                        .weirdness(ClimateParameter.point(-0.95))
                        .depth(ClimateParameter.zero())
                        .continentalness(ClimateParameter.point(1.0))
                        .build()
                )
                .add(
                    lunarMaria, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.point(-1.0))
                        .weirdness(ClimateParameter.point(-0.95))
                        .depth(ClimateParameter.zero())
                        .continentalness(ClimateParameter.point(-1.0))
                        .build()
                )
                .add(
                    lunarFarSide, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.point(1.0))
                        .weirdness(ClimateParameter.point(-0.95))
                        .depth(ClimateParameter.zero())
                        .continentalness(ClimateParameter.point(-0.6))
                        .build()
                )
                .add(
                    impactBasin, ClimatePoint.builder()
                        .temperature(ClimateParameter.zero())
                        .humidity(ClimateParameter.zero())
                        .erosion(ClimateParameter.point(1.0))
                        .weirdness(ClimateParameter.point(1.0))
                        .depth(ClimateParameter.zero())
                        .continentalness(ClimateParameter.point(-0.6))
                        .build()
                )
                .build()
        )
        .noise(
            Noise.builder()
                .seaLevel(0)
                .oreVeinsEnabled(true)
                .aquifersEnabled(false)
                .defaultBlock(Material.ANDESITE)
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
                        .erosion(DensityFunction.noise(erosionNoise, 1.0, 0.0).flatCache().clamp(-1.0, 1.0))
                        .depth(DensityFunction.zero())
                        .ridges(impactBasinNoise.flatCache())

                        .veinToggle(DensityFunction.constant(-1.0))
                        .veinRidged(DensityFunction.constant(0.0))
                        .veinGap(DensityFunction.constant(0.0))

                        .preliminarySurfaceLevel(DensityFunction.constant(-100.0))
                        .finalDensity(
                            DensityFunction.yClampedGradient(MIN_HEIGHT, 200, 1.0, -1.0)
                                    + DensityFunction.noise(mainNoise, 1.0, 0.0).flatCache().interpolated()
                                .quarterNegative()
                                    - impactBasinNoise.flatCache().interpolated()
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
                            SurfaceRule.sequence(
                                SurfaceRule.ifTrue(
                                    SurfaceRule.isBiome(lunarMaria),
                                    SurfaceRule.block(Material.GRAY_CONCRETE)
                                ),
                                SurfaceRule.ifTrue(
                                    SurfaceRule.not(SurfaceRule.isBiome(lunarMaria)),
                                    SurfaceRule.block(Material.GRAY_CONCRETE_POWDER)
                                )
                            )
                        )
                    )
                )
                .build()
        )
        .build()

    private const val DAY_LENGTH_TICKS = 708734
    private const val MIN_HEIGHT = -64
    private const val MAX_HEIGHT = 320
}