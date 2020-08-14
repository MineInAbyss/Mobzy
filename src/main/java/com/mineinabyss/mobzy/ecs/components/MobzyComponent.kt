package com.mineinabyss.mobzy.ecs.components

import kotlinx.serialization.Polymorphic

@Polymorphic
interface MobzyComponent

@Polymorphic
interface SerializableComponent: MobzyComponent{
    val copy: () -> MobzyComponent
}