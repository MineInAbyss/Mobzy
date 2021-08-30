package com.mineinabyss.mobzy.registration

import com.comphenix.protocol.PacketType.Play.Server
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.enums.PacketEntityType
import com.mineinabyss.protocolburrito.packets.ClientboundAddMobPacket
import com.mineinabyss.protocolburrito.packets.ClientboundMoveEntityPacket
import org.bukkit.Bukkit

object MobzyPacketInterception {
    fun registerPacketInterceptors() {
        protocolManager(mobzy) {
            //send zombie as entity type for custom mobs
            onSend(
                ::ClientboundAddMobPacket,
                Server.SPAWN_ENTITY_LIVING
            ) {
                val entity = Bukkit.getEntity(uuid) ?: return@onSend
                if (geary(entity).has<Model>())
                    type = PacketEntityType.ZOMBIE.id
            }

            //pitch lock custom mobs
            onSend(
                ::ClientboundMoveEntityPacket,
                //all these packets seem to be enough to cover all head rotations
                Server.ENTITY_LOOK,
                Server.REL_ENTITY_MOVE_LOOK,
                Server.LOOK_AT,
                Server.ENTITY_TELEPORT
            ) {
                //TODO change entity(entityId) to return nullable in ProtocolBurrito
                val entity = getEntityFromID(it.player.world, entityId) ?: return@onSend
                if (gearyOrNull(entity)?.has<Model>() == true) //check mob involved
                    xRot = 0
            }


            //TODO if entity has custom sound effects component, prevent entity sound packets
            // Uncomment when new sound system is ready
            /*onSend(Server.ENTITY_SOUND) {
                PacketEntitySoundEffect(packet).apply {
                    if(gearyOrNull(entity(entityId))?.has<Sounds>() == true)
                        isCancelled = true
                }
            }*/
        }
    }
}
