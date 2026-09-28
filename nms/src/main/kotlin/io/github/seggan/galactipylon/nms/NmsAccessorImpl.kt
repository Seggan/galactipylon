package io.github.seggan.galactipylon.nms

import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.Entity
import java.lang.invoke.MethodHandles
import net.minecraft.world.entity.Entity as NmsEntity

@Suppress("unused")
object NmsAccessorImpl : NmsAccessor {

    private val entityLookup = MethodHandles.privateLookupIn(NmsEntity::class.java, MethodHandles.lookup())

    private val entityAirDragHandle = entityLookup.unreflect(NmsEntity::class.java.getDeclaredMethod("getAirDrag"))

    override fun getEntityAirDrag(entity: Entity): Double {
        return (entityAirDragHandle.invoke((entity as CraftEntity).handle) as Float).toDouble()
    }

    override fun getEntityGravity(entity: Entity): Double {
        return (entity as CraftEntity).handle.gravity
    }
}