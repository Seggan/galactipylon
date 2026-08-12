package io.github.seggan.galactipylon

import io.github.seggan.galactipylon.worldgen.feature.Crater
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage", "unused")
class Bootstrapper : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY) { event ->
            event.registrar().discoverPack(this::class.java.getResource("/datapack")!!.toURI(), "provided")
        }
        Crater.register()
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin = Galactipylon
}