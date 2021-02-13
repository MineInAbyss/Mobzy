package com.mineinabyss.mobzy.registration

import com.comphenix.protocol.PacketType.Play.Server
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.mobzy.api.isCustomEntity
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.enums.PacketEntityType
import com.mineinabyss.protocolburrito.packets.PacketEntityLook
import com.mineinabyss.protocolburrito.packets.PacketSpawnEntityLiving
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType

object MobzyPacketInterception {
    fun registerPacketInterceptors() {
        protocolManager(mobzy) {
            //send zombie as entity type for custom mobs
            onSend(Server.SPAWN_ENTITY_LIVING) {
                PacketSpawnEntityLiving(packet).apply {
                    if (entity(entityUUID)?.isCustomMob == true)
                        type = PacketEntityType.ZOMBIE.id
                }
            }
            onSend(Server.SPAWN_ENTITY) {
                Bukkit.getEntity(packet.uuiDs.read(0))?.with<GearyEntityType> {
                    if ((it as MobType).baseClass == "mobzy:projectile") {
                        packet.entityTypeModifier.write(0, EntityType.SNOWBALL)
                    }
                }
            }

            //pitch lock custom mobs
            onSend(
                //all these packets seem to be enough to cover all head rotations
                Server.ENTITY_LOOK,
                Server.REL_ENTITY_MOVE_LOOK,
                Server.LOOK_AT,
                Server.ENTITY_TELEPORT
            ) {
                PacketEntityLook(packet).apply {
                    if (entity(entityId).isCustomEntity) //check entity involved
                        pitch = 0
                }
            }
        }
    }
}