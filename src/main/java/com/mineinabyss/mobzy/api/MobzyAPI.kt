@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.mobzyConfig

fun MobzyAddon.registerAddonWithMobzy() {
    if (!mobzyConfig.registeredAddons.contains(this)) mobzyConfig.registeredAddons += this
}