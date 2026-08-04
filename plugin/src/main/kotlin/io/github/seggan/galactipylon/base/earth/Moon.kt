package io.github.seggan.galactipylon.base.earth

import dev.wyck.biome.Biome
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
import dev.wyck.worldgen.biome.BiomeSource
import dev.wyck.worldgen.chunk.ChunkGenerator
import dev.wyck.worldgen.chunk.flat.FlatLevelGeneratorSettings
import io.github.seggan.galactipylon.DEGREES
import io.github.seggan.galactipylon.asResourceKey
import io.github.seggan.galactipylon.celestials.property.Orbit
import io.github.seggan.galactipylon.celestials.world.AlienWorld
import io.github.seggan.galactipylon.key
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
        .minY(-64)
        .height(384)
        .logicalHeight(384)
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
        .register()

    override val generator = ChunkGenerator.flat()
        .settings(FlatLevelGeneratorSettings.DESERT)
        .biomeSource(BiomeSource.fixed(lunarMaria))
        .build()

    private const val DAY_LENGTH_TICKS = 708734
}