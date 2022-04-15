package com.mineinabyss.mobzy.systems.packets

import com.comphenix.protocol.PacketType.Play.Server
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.protocolburrito.dsl.protocolManager
import com.mineinabyss.protocolburrito.dsl.sendTo
import com.mineinabyss.protocolburrito.packets.wrap
import com.okkero.skedule.schedule
import kotlinx.coroutines.delay
import net.minecraft.core.Registry
import net.minecraft.network.protocol.game.ClientboundAddMobPacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import org.bukkit.Location
import org.bukkit.SoundCategory
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
            onSend(Server.SPAWN_ENTITY_LIVING) {
                val nms = (packet.handle as ClientboundAddMobPacket).wrap()
                val entity = entity(nms.id)
                if (entity.toGeary().has<Model>())
                    nms.type = Registry.ENTITY_TYPE.getId(NMSEntityType.ARMOR_STAND)

            }

            onSend(Server.ENTITY_METADATA) {
                val nms = (packet.handle as ClientboundSetEntityDataPacket).wrap()
                val entity: Entity = getEntityFromID(player.world, nms.id) ?: return@onSend
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
                            val nmsEntity = entity.toNMS()
                            packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
                            PacketContainer(
                                Server.REL_ENTITY_MOVE,
                                ClientboundMoveEntityPacket.Pos(nms.id, 0, -50, 0, true)
                            ).sendTo(player)
                            packet.sendTo(player)
                            delay(5)
                        }
                    }
                }

                packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
            }

            onSend(
                Server.NAMED_SOUND_EFFECT
            ) {
                val nms = (packet.handle as ClientboundSoundPacket).wrap()
                val loc = Location(
                    player.world,
                    nms.x / 8.0,
                    nms.y / 8.0,
                    nms.z / 8.0
                )
                val entity = player.world.getNearbyEntities(
                    loc, 1.0, 1.0, 1.0
                ).first().toGeary()
                entity.with { sounds: Sounds ->
                    isCancelled = true
                    val path = nms.sound.location.path
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
                            SoundCategory.valueOf(nms.source.getName().uppercase()),
                            nms.volume,
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
