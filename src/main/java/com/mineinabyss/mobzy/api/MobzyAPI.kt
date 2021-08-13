package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.mobzyConfig

fun MobzyAddon.registerAddonWithMobzy() {
    if (!MobzyConfig.registeredAddons.contains(this)) MobzyConfig.registeredAddons += this
}
