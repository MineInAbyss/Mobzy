package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.interaction.ProjectileShootAt
import org.bukkit.Sound
import org.bukkit.entity.Snowball
import kotlin.random.Random

class ProjectileShootingSystem : TickingSystem() {
    val target by get<ProjectileShootAt>()
    val entity by get<BukkitEntity>()

    override fun GearyEntity.tick() {
        val loc = entity.location
        val (x, y, z) = loc

        val targetLoc = target.location
        val dX = targetLoc.x - x
        val dY = targetLoc.y - y - 0.4
        val dZ = targetLoc.z - z

        loc.world.playSound(
            loc,
            Sound.ENTITY_SNOW_GOLEM_SHOOT,
            1.0f,
            1.0f / (Random.nextDouble(0.8, 1.2).toFloat())
        )

        (entity as Snowball).toNMS().shoot(dX, dY, dZ, 1.6f, 12.0f)

        remove<ProjectileShootAt>()
    }
}
