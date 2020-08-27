package com.mineinabyss.mobzy.api.pathfindergoals

import net.minecraft.server.v1_16_R2.ControllerMove

val ControllerMove.targetX
    get() = d()
val ControllerMove.targetY
    get() = e()
val ControllerMove.targetZ
    get() = f()
val ControllerMove.speed
    get() = c()

fun ControllerMove.moveTo(x: Double, y: Double, z: Double, speed: Double) = a(x, y, z, speed)