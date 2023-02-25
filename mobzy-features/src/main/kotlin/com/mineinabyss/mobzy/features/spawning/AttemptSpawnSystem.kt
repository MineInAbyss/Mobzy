package com.mineinabyss.mobzy.features.spawning

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.features.spawning.components.AttemptSpawn
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.event.entity.CreatureSpawnEvent

object AttemptSpawnSystem : GearyListener() {
    val TargetScope.entityType by onSet<NMSEntityType<*>>()
    val EventScope.attemptSpawn by onSet<AttemptSpawn>()

    //    val TargetScope.type by get<MobzyType>()
    val TargetScope.family by family {
        not { has<BukkitEntity>() }
    }

    @Handler
    fun TargetScope.handle(event: EventScope) {
        val loc = event.attemptSpawn.location
        val world = loc.world.toNMS()
        val spawned = entityType.create(world) ?: return
        spawned.moveTo(loc.x, loc.y, loc.z)

        if (spawned is Mob) {
            spawned.finalizeSpawn(
                world,
                world.getCurrentDifficultyAt(spawned.blockPosition()),
                MobSpawnType.COMMAND,
                null,
                null
            )
        }
        world.addFreshEntityWithPassengers(spawned, CreatureSpawnEvent.SpawnReason.COMMAND)
        spawned.toBukkit()
    }
}
