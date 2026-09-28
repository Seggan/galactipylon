package io.github.seggan.galactipylon.nms

import org.bukkit.entity.Entity

interface NmsAccessor {

    fun getEntityAirDrag(entity: Entity): Double
    fun getEntityGravity(entity: Entity): Double

    companion object {
        val instance = Class.forName("io.github.seggan.galactipylon.nms.NmsAccessorImpl")
            .getField("INSTANCE")
            .get(null) as NmsAccessor
    }
}

val Entity.airDrag get() = NmsAccessor.instance.getEntityAirDrag(this)
val Entity.gravity get() = NmsAccessor.instance.getEntityGravity(this)