package com.mineinabyss.mobzy.systems.packets

import com.comphenix.protocol.PacketType.Play.Server
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.packets.wrap
import net.minecraft.core.Registry
import net.minecraft.network.protocol.game.ClientboundAddMobPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import org.bukkit.entity.Entity
import kotlin.experimental.or


object MobzyPacketInterception {
    private val byteSerializer = WrappedDataWatcher.Registry.get(Class.forName("java.lang.Byte"))
    private val vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer()
    private const val META_ENTITY_FLAGS = 0
    private const val META_ARMORSTAND = 15
    fun registerPacketInterceptors() {
        protocolManager(mobzy) {
            //send zombie as entity type for custom mobs
            onSend(Server.SPAWN_ENTITY_LIVING) {
                val nms = (packet.handle as ClientboundAddMobPacket).wrap()
                val entity = entity(nms.id)
                if (entity.toGeary().has<Model>())
                    nms.type = Registry.ENTITY_TYPE.getId(NMSEntityType.ARMOR_STAND)

            }

            onSend(Server.ENTITY_METADATA) {
                val nms = (packet.handle as ClientboundSetEntityDataPacket).wrap()
                val entity: Entity = getEntityFromID(player.world, nms.id) ?: return@onSend
                if (!entity.toGeary().has<Model>()) return@onSend

                val existingMeta = WrappedDataWatcher(packet.watchableCollectionModifier.values[0])
                val metadata = WrappedDataWatcher().apply {
                    val entityFlags = existingMeta.getObject(META_ENTITY_FLAGS) as? Byte ?: 0

                    // Ensure invisible but keep other parts (glowing) if active
                    setObject(
                        WrappedDataWatcherObject(META_ENTITY_FLAGS, byteSerializer),
                        entityFlags or 0x20
                    )

                    //Maker, no base pate
                    setObject(
                        WrappedDataWatcherObject(META_ARMORSTAND, byteSerializer),
                        (0x08 or 0x10).toByte()
                    )

                    //Tilt head on death
                    if (entity.isDead)
                        setObject(
                            WrappedDataWatcherObject(16, vectorSerializer),
                            Vector3F(0f, 0f, 45f)
                        )

                    //Don't copy anything else to prevent crashes in bad packets.
                }

                packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
            }

            //pitch lock custom mobs
//            onSend(
//                //all these packets seem to be enough to cover all head rotations
//                Server.ENTITY_LOOK,
//                Server.REL_ENTITY_MOVE_LOOK,
//                Server.LOOK_AT,
//                Server.ENTITY_TELEPORT
//            ) {
//                val nms = (packet.handle as ClientboundMoveEntityPacket).wrap()
//                //TODO change entity(entityId) to return nullable in ProtocolBurrito
//                val entity = getEntityFromID(player.world, nms.entityId) ?: return@onSend
//                if (entity.toGearyOrNull()?.has<Model>() == true) //check mob involved
//                    nms.xRot = 0
//            }


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
