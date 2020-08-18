package com.mineinabyss.geary.ecs

import kotlinx.serialization.Polymorphic

@Polymorphic
interface MobzyComponent

//TODO consider DeepCopy https://github.com/enbandari/KotlinDeepCopy
@Polymorphic
interface CopyableComponent: MobzyComponent {
    val copy: () -> MobzyComponent
}