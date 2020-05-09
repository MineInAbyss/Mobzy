@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.MobzyConfig

fun MobzyAddon.registerAddonWithMobzy() {
    if (!MobzyConfig.registeredAddons.contains(this)) MobzyConfig.registeredAddons += this
}