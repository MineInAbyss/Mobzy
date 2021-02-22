package com.mineinabyss.mobzy.registration

import com.comphenix.protocol.PacketType.Play.Server
import com.mineinabyss.geary.ecs.components.GearyPrefab
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.enums.PacketEntityType
import com.mineinabyss.protocolburrito.packets.PacketEntityLook
import com.mineinabyss.protocolburrito.packets.PacketSpawnEntity
import com.mineinabyss.protocolburrito.packets.PacketSpawnEntityLiving
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
                PacketSpawnEntity(packet).apply{
                    entity(entityId).with<GearyPrefab>{
                        //FIXME ProtocolBurrito doesn't work because of an NMS inconsistency here
                        //TODO make a component to allow overriding the type here
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
                    if (entity(entityId).isCustomMob) //check mob involved
                        pitch = 0
                }
            }
        }
    }
}
