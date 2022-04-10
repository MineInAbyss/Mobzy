package com.mineinabyss.mobzy.injection

import org.koin.mp.KoinPlatformTools

interface NMSTypeInjectorContext {
    val nmsTypeInjector: MobzyNMSTypeInjector
}

var globalNMSTypeInjector = KoinPlatformTools.defaultContext().get().get<MobzyNMSTypeInjector>()
