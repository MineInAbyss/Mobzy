package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R2.ControllerMove
import org.bukkit.entity.Mob

abstract class MoveController(mob: Mob) : ControllerMove(mob.toNMS<NMSEntityInsentient>()) {
    var targetX
        get() = b
        set(value) {
            b = value
        }
    var targetY
        get() = c
        set(value) {
            b = value
        }
    var targetZ
        get() = d
        set(value) {
            d = value
        }
    var speed
        get() = e
        set(value) {
            e = value
        }
}