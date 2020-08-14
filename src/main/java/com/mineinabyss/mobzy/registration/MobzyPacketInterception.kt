package com.mineinabyss.mobzy.registration

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.mobzy
import org.bukkit.Bukkit

object MobzyPacketInterception {
    fun registerPacketInterceptors() {
        val protocolManager = ProtocolLibrary.getProtocolManager()!!

        //send zombie as entity type for custom mobs
        protocolManager.addPacketListener(object : PacketAdapter(mobzy, ListenerPriority.NORMAL,
                PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            override fun onPacketSending(event: PacketEvent) {
                if (Bukkit.getEntity(event.packet.uuiDs.read(0))?.isCustomMob == true)
                    event.packet.integers.write(1, 101)
            }
        })

        //pitch lock custom mobs
        protocolManager.addPacketListener(object : PacketAdapter(mobzy, ListenerPriority.NORMAL,
                //all these packets seem to be enough to cover all head rotations
                PacketType.Play.Server.ENTITY_LOOK,
                PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
                PacketType.Play.Server.LOOK_AT,
                PacketType.Play.Server.ENTITY_TELEPORT
        ) {
            override fun onPacketSending(event: PacketEvent) {
                if (event.packet.getEntityModifier(event).read(0).isCustomMob) //check entity involved
                    event.packet.bytes.write(1, 0) //modify pitch to be zero
            }
        })
    }
}