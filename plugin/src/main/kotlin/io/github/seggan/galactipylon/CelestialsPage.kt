package io.github.seggan.galactipylon

import io.github.pylonmc.rebar.guide.button.PageButton
import io.github.pylonmc.rebar.guide.pages.base.SimpleDynamicGuidePage
import io.github.seggan.galactipylon.celestials.CelestialObject
import java.util.*

class OrbitersPage(obj: CelestialObject) : SimpleDynamicGuidePage(obj.key, {
    obj.orbiters.sortedBy { it.orbit.semimajorAxis }.map { PageButton(it.displayItem, pages.computeIfAbsent(it, ::OrbitersPage)) }
}) {

    init {
        pages[obj] = this
    }

    override val title = obj.name
}

private val pages = WeakHashMap<CelestialObject, OrbitersPage>()