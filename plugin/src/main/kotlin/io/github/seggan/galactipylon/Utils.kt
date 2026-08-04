package io.github.seggan.galactipylon

import dev.wyck.keys.ResourceKey
import net.kyori.adventure.key.Key

fun Key.asResourceKey() = ResourceKey.of(namespace(), value())