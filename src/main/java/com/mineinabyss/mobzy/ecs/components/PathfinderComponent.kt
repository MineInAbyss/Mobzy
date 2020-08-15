package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlinx.serialization.Polymorphic
import net.minecraft.server.v1_16_R1.PathfinderGoal
import org.bukkit.entity.Mob

@Polymorphic
interface PathfinderComponent : MobzyComponent {
    fun createPathfinder(mob: Mob): NMSPathfinderGoal
}