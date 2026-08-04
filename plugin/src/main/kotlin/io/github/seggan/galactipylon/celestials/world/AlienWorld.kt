package io.github.seggan.galactipylon.celestials.world

import dev.wyck.level.LevelCreator
import dev.wyck.level.dimension.Dimension
import dev.wyck.level.entity.LevelSpawner
import dev.wyck.worldgen.chunk.ChunkGenerator
import io.github.seggan.galactipylon.GalactipylonRegistry
import io.github.seggan.galactipylon.asResourceKey
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World

abstract class AlienWorld(key: NamespacedKey) : PlanetaryWorld(key) {

    abstract val dimension: Dimension
    open val spawners: List<LevelSpawner> = emptyList()
    abstract val generator: ChunkGenerator
    open val generateStructures = false

    override fun loadWorld(): World {
        Bukkit.getWorld(key)?.let { return it }
        return LevelCreator.builder()
            .resourceKey(key.asResourceKey())
            .name(worldName)
            .generateStructures(generateStructures)
            .spawners(spawners)
            .dimension(dimension)
            .generator(generator)
            .create()
    }

    override fun register() {
        super.register()
        GalactipylonRegistry.ALIEN_WORLDS.register(this)
    }
}