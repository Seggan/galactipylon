package io.github.seggan.galactipylon.worldgen.feature

import dev.wyck.keys.ResourceKey
import dev.wyck.worldgen.feature.custom.CustomFeature
import dev.wyck.worldgen.feature.custom.PlacementContext
import org.bukkit.Material
import org.bukkit.util.BlockVector
import kotlin.math.ceil
import kotlin.math.roundToInt

object Crater : CustomFeature<Crater.Config>(Config::defaults, ResourceKey.of("galactipylon", "crater")) {

    override fun place(context: PlacementContext<Config>): Boolean {
        val config = context.config()
        val random = context.random()
        val origin = context.origin()

        val depth = random.nextInt(config.depth.first, config.depth.last)
        val width = random.nextInt(config.width.first, config.width.last)
        val radius = width / 2.0
        val ceilRadius = ceil(radius).toInt()

        for (x in -ceilRadius until ceilRadius) {
            for (z in -ceilRadius until ceilRadius) {
                val vector = BlockVector(origin.x + x, origin.y, origin.z + z)

                val depthHere = craterDepth(vector.distanceSquared(origin), radius, depth)
                if (depthHere <= 0) continue

                var type = context.getBlock(vector)
                while (type.material != Material.AIR) {
                    vector.y += 1
                    type = context.getBlock(vector)
                }
                while (type.material == Material.AIR) {
                    vector.y -= 1
                    type = context.getBlock(vector)
                }

                val bottom = BlockVector(vector.x, vector.y - depthHere, vector.z)
                repeat(3) {
                    context.setBlock(bottom, type)
                    bottom.y -= 1
                }
                for (y in 0 until depthHere) {
                    context.removeBlock(BlockVector(vector.x, vector.y - y, vector.z))
                }
            }
        }

        return true
    }

    private fun craterDepth(radiusSq: Double, radius: Double, depth: Int): Int {
        return (depth * (1 - radiusSq / (radius * radius))).roundToInt()
    }

    data class Config(val depth: IntRange, val width: IntRange) {
        companion object {
            fun defaults() = Config(0..0, 0..0)
        }
    }
}