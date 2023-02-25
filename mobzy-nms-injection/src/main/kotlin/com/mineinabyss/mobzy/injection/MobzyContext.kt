package com.mineinabyss.mobzy.injection

import com.mineinabyss.idofront.di.DI

interface NMSTypeInjectorContext {
    val nmsTypeInjector: MobzyNMSTypeInjector
}

val nmsTypeInjector by DI.observe<NMSTypeInjectorContext>()
