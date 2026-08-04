package io.github.seggan.galactipylon.guide

import io.github.pylonmc.rebar.guide.pages.base.SimpleDynamicGuidePage
import io.github.seggan.galactipylon.celestials.CelestialObject
import java.util.*

class OrbitersPage private constructor(obj: CelestialObject) : SimpleDynamicGuidePage(obj.key, {
    obj.orbiters.sortedBy { it.orbit.semimajorAxis }.map { CelestialButton(it) }
}) {

    init {
        pages[obj] = this
    }

    override val title = obj.name

    companion object {
        operator fun invoke(obj: CelestialObject): OrbitersPage = pages.computeIfAbsent(obj, ::OrbitersPage)
    }
}

private val pages = WeakHashMap<CelestialObject, OrbitersPage>()