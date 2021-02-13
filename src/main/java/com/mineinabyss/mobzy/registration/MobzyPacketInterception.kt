package com.mineinabyss.mobzy.registration

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.PacketType.Play.Server
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.ecs.components.initialization.PacketEntity
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobzy
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

object MobzyPacketInterception {
    fun registerPacketInterceptors() {
        protocolManager(mobzy) {
            onSend(Server.SPAWN_ENTITY_LIVING) {
                Bukkit.getEntity(packet.uuiDs.read(0))?.with<PacketEntity> {
                    packet.integers.write(1, it.typeId)
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
                //all these packets seem to be enough to cover all head rotationsS
                Server.ENTITY_LOOK,
                Server.REL_ENTITY_MOVE_LOOK,
                Server.LOOK_AT,
                Server.ENTITY_TELEPORT
            ) {
                if (packet.getEntityModifier(this).read(0).isCustomMob) //check entity involved
                    packet.bytes.write(1, 0) //modify pitch to be zero
            }
        }
    }
}

class ProtoKolManager(protocolManager: ProtocolManager, val plugin: Plugin) : ProtocolManager by protocolManager {

    fun ProtocolManager.onSend(
        vararg packets: PacketType,
        priority: ListenerPriority = ListenerPriority.NORMAL,
        onSend: PacketEvent.() -> Unit
    ) {
        addPacketListener(object : PacketAdapter(plugin, priority, *packets) {
            override fun onPacketSending(event: PacketEvent) {
                onSend(event)
            }
        })
    }

    fun ProtocolManager.onReceive(
        vararg packets: PacketType,
        priority: ListenerPriority = ListenerPriority.NORMAL,
        onSend: PacketEvent.() -> Unit
    ) {
        addPacketListener(object : PacketAdapter(plugin, priority, *packets) {
            override fun onPacketReceiving(event: PacketEvent) {
                onSend(event)
            }
        })
    }
}

fun protocolManager(plugin: Plugin, run: ProtoKolManager.() -> Unit) {
    ProtoKolManager(ProtocolLibrary.getProtocolManager()!!, plugin).apply(run)
}

fun PacketContainer.sendTo(player: Player) {
    ProtocolLibrary.getProtocolManager()!!.sendServerPacket(player, this)
}
