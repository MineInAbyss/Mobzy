package com.mineinabyss.mobzy.systems.packets

import com.comphenix.protocol.PacketType.Play.Server
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.ScheduledPacket
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.dsl.sendTo
import com.mineinabyss.protocolburrito.packets.ClientboundAddEntityPacketWrap
import com.mineinabyss.protocolburrito.packets.ClientboundSetEntityDataPacketWrap
import com.mineinabyss.protocolburrito.packets.ClientboundSetEquipmentPacketWrap
import kotlinx.coroutines.delay
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.entity.Entity
import kotlin.experimental.or

class PlayingDeathAnimation

object MobzyPacketInterception {
    private val byteSerializer = WrappedDataWatcher.Registry.get(Class.forName("java.lang.Byte"))
    private val vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer()
    private const val META_ENTITY_FLAGS = 0
    private const val META_ARMORSTAND = 15
    fun registerPacketInterceptors() {

        protocolManager(mobzy) {
            //send zombie as entity type for custom mobs
            onSend<ClientboundAddEntityPacketWrap> { wrap ->
                val entity = runCatching { entity(wrap.id) }.getOrElse {
                    return@onSend
                }//() ?: return@onSend
                val geary = entity.toGeary()

                geary.with { model: ModelEngineComponent ->
                    entity.toModelEntity()?.addPlayer(player)
                    isCancelled = true
                }
                if (geary.has<Model>())
                    wrap.type = NMSEntityType.ARMOR_STAND
            }

            onSend<ClientboundSetEquipmentPacketWrap> { wrap ->
                val entity: Entity = getEntityFromID(player.world, wrap.entity) ?: return@onSend
                val geary = entity.toGeary()
                geary.with { model: Model ->
                    wrap.slots = wrap.slots.toMutableList().apply {
                        removeIf { it.first == EquipmentSlot.HEAD }
                        add(
                            com.mojang.datafixers.util.Pair(
                                EquipmentSlot.HEAD,
                                CraftItemStack.asNMSCopy(model.modelItemStack)
                            )
                        )
                    }
                }
            }

            if (MobzyConfig.data.supportNonMEEntities) onSend<ClientboundSetEntityDataPacketWrap> { wrap ->
                val entity: Entity = getEntityFromID(player.world, wrap.id) ?: return@onSend
                val geary = entity.toGeary()
                if (!geary.has<Model>() || geary.has<PlayingDeathAnimation>()) return@onSend

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

                    //Don't copy anything else to prevent crashes from bad packets
                }

                //Tilt head on death
                if (entity.isDead) {
                    geary.add<PlayingDeathAnimation>()
                    mobzy.launch {
                        var rot = 0f
                        repeat(10) {
                            rot += 10f
                            metadata.setObject(
                                WrappedDataWatcherObject(16, vectorSerializer),
                                Vector3F(0f, 0f, rot)
                            )
                            packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
                            PacketContainer(
                                Server.REL_ENTITY_MOVE,
                                ClientboundMoveEntityPacket.Pos(wrap.id, 0, -55, 0, true)
                            ).sendTo(player)
                            packet.sendTo(player)
                            delay(1.ticks)
                        }
                    }
                }
                packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
                schedule(
                    ScheduledPacket.fromFiltered(
                        PacketContainer(Server.ENTITY_EQUIPMENT, ClientboundSetEquipmentPacket(wrap.id, listOf())),
                        player
                    )
                )
            }
        }
    }
}
