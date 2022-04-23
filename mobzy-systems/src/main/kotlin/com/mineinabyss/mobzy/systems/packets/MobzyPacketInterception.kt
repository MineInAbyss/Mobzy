package com.mineinabyss.mobzy.systems.packets

import com.comphenix.protocol.PacketType.Play.Server
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.ScheduledPacket
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.dsl.sendTo
import com.mineinabyss.protocolburrito.packets.*
import com.okkero.skedule.schedule
import kotlinx.coroutines.delay
import net.minecraft.core.Registry
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Location
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Entity
import kotlin.experimental.or
import kotlin.random.Random

class PlayingDeathAnimation

object MobzyPacketInterception {
    private val byteSerializer = WrappedDataWatcher.Registry.get(Class.forName("java.lang.Byte"))
    private val vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer()
    private const val META_ENTITY_FLAGS = 0
    private const val META_ARMORSTAND = 15

    fun registerPacketInterceptors() {
        protocolManager(mobzy) {
            //send zombie as entity type for custom mobs
            onSend<ClientboundAddMobPacketWrap> { wrap ->
                val entity = entity(wrap.id)
                if (entity.toGeary().has<Model>())
                    wrap.type = Registry.ENTITY_TYPE.getId(NMSEntityType.ARMOR_STAND)
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

            onSend<ClientboundSetEntityDataPacketWrap> { wrap ->
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

//                    if (entity.isDead) {
//                        setObject(
//                            WrappedDataWatcherObject(16, vectorSerializer),
//                            Vector3F(0f, 0f, 45f)
//                        )
//                    }

                    //Don't copy anything else to prevent crashes in bad packets.
                }

                //Tilt head on death
                if (entity.isDead) {
                    geary.add<PlayingDeathAnimation>()
                    mobzy.schedule {
                        delay(1)
                        var rot = 0f
                        repeat(90) {
                            rot += 1f
                            metadata.setObject(
                                WrappedDataWatcherObject(16, vectorSerializer),
                                Vector3F(0f, 0f, rot)
                            )
                            packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
                            PacketContainer(
                                Server.REL_ENTITY_MOVE,
                                ClientboundMoveEntityPacket.Pos(wrap.id, 0, -50, 0, true)
                            ).sendTo(player)
                            packet.sendTo(player)
                            delay(5)
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

            onSend<ClientboundCustomSoundPacketWrap> {
                it.name

            }
            onSend<ClientboundSoundEntityPacketWrap> { wrap ->
                val entity = entity(wrap.id)
                wrap.sound
            }

            onSend<ClientboundSoundPacketWrap> { wrap ->
                val loc = Location(
                    player.world,
                    wrap.x / 8.0,
                    wrap.y / 8.0,
                    wrap.z / 8.0
                )
                val entity = player.world.getNearbyEntities(
                    loc, 1.0, 1.0, 1.0
                ).firstOrNull()?.toGeary() ?: return@onSend
                entity.with { sounds: Sounds ->
                    isCancelled = true
                    val path = wrap.sound.location.path
                    val sound = when {
                        ".step" in path -> sounds.step
                        ".ambient" in path -> sounds.ambient
                        ".death" in path -> sounds.death
                        ".hurt" in path -> sounds.hurt
                        ".splash" in path -> sounds.splash
                        ".swim" in path -> sounds.swim
                        else -> null
                    }
                    if (sound != null) {
                        player.world.playSound(
                            loc,
                            sound,
                            SoundCategory.valueOf(wrap.source.getName().uppercase()),
                            wrap.volume,
                            (sounds.pitch + (Random.nextDouble(-sounds.pitchRange, sounds.pitchRange))).toFloat()
                        )
                        return@onSend
                    }
                }
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
