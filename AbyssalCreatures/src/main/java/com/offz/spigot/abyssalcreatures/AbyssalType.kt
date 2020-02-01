package com.offz.spigot.abyssalcreatures

import com.offz.spigot.abyssalcreatures.mobs.flying.*
import com.offz.spigot.abyssalcreatures.mobs.hostile.*
import com.offz.spigot.abyssalcreatures.mobs.passive.*
import com.offz.spigot.mobzy.registerEntity
import com.offz.spigot.mobzy.registerTypes
import com.offz.spigot.mobzy.toEntityTypeID
import net.minecraft.server.v1_15_R1.EnumCreatureType

/**
 * (..., func = ::Neritantan)   is the same as   (...){ world -> Neritantan(world) }   or   (...){ Neritantan(it) }
 */
@Suppress("SpellCheckingInspection", "PropertyName", "unused")
class AbyssalType {
    //TODO get these to be static somehow, maybe an enum?
    //Passive
    val ASHIMITE = registerEntity("ashimite", EnumCreatureType.CREATURE, width = 2f, height = 2f, func = ::Ashimite)
    val FUWAGI = registerEntity("fuwagi", EnumCreatureType.CREATURE, width = 0.6f, height = 0.6f, func = ::Fuwagi)
    val MAKIHIGE = registerEntity("makihige", EnumCreatureType.WATER_CREATURE, width = 2f, height = 2f, func = ::Makihige)
    val NERITANTAN = registerEntity("neritantan", EnumCreatureType.CREATURE, width = 0.6f, height = 0.6f, func = ::Neritantan)
    val OKIBO = registerEntity("okibo", EnumCreatureType.CREATURE, width = 3f, height = 3f, func = ::Okibo)

    //Hostile
    val INBYO = registerEntity("inbyo", EnumCreatureType.MONSTER, width = 0.6f, height = 3f, func = ::Inbyo)
    val KUONGATARI = registerEntity("kuongatari", EnumCreatureType.MONSTER, width = 0.6f, height = 0.6f, func = ::Kuongatari)
    val OTTOBAS = registerEntity("ottobas", EnumCreatureType.MONSTER, width = 2f, height = 3f, func = ::Ottobas)
    val SILKFANG = registerEntity("silkfang", EnumCreatureType.MONSTER, width = 2f, height = 2f, func = ::Silkfang)
    val STEVE = registerEntity("steve", EnumCreatureType.MONSTER, width = 2f, height = 7f, func = ::Steve)
    val TAMAUGACHI = registerEntity("tamaugachi", EnumCreatureType.MONSTER, width = 2.5f, height = 2.5f, func = ::Tamaugachi)
    val TESUCHI = registerEntity("tesuchi", EnumCreatureType.MONSTER, width = 0.6f, height = 0.6f, func = ::Tesuchi)

    //Flying
    //TODO I don't like using MISC for flying creatures, but this is how we're setting them to a separate mob cap for now
    val BENIKUCHINAWA = registerEntity("benikuchinawa", EnumCreatureType.MISC, width = 4.5f, height = 2f, func = ::Benikuchinawa)
    val CORPSE_WEEPER = registerEntity("corpse_weeper", EnumCreatureType.MISC, width = 3f, height = 3f, func = ::CorpseWeeper)
    val CYATORIA = registerEntity("cyatoria", EnumCreatureType.MISC, width = 3f, height = 2f, func = ::Cyatoria)
    val DOSETORI = registerEntity("dosetori", EnumCreatureType.MISC, width = 3f, height = 2f, func = ::Dosetori)
    val HAMMERBEAK = registerEntity("hammerbeak", EnumCreatureType.MISC, width = 3f, height = 2f, func = ::Hammerbeak)
    val KAZURA = registerEntity("kazura", EnumCreatureType.WATER_CREATURE, width = 1f, height = 1f, func = ::Kazura)
    val MADOKAJACK = registerEntity("madokajack", EnumCreatureType.MISC, width = 4.5f, height = 3f, func = ::Madokajack)
    val ROHANA = registerEntity("rohana", EnumCreatureType.WATER_CREATURE, width = 0.6f, height = 0.6f, func = ::Rohana)

    //NPCs
    fun registerNPC(name: String, modelID: Int) = registerEntity(name.toEntityTypeID(), EnumCreatureType.MISC, templateName = "npc", width = 0.6f, height = 2f) { NPC(it, name, modelID) }

    val MITTY = registerNPC("Mitty", 2)
    val NANACHI = registerNPC("Nanachi", 3)
    val BONDREWD = registerNPC("Bondrewd", 4)
    val HABO = registerNPC("Habo", 5)
    val JIRUO = registerNPC("Jiruo", 6)
    val KIYUI = registerNPC("Kiyui", 7)
    val MARULK = registerNPC("Marulk", 8)
    val NAT = registerNPC("Nat", 9)
    val OZEN = registerNPC("Ozen", 10)
    val REG = registerNPC("Reg", 11)
    val RIKO = registerNPC("Riko", 12)
    val SHIGGY = registerNPC("Shiggy", 13)
    val TORKA = registerNPC("Torka", 14)
    val LYZA = registerNPC("Lyza", 15)
    val PRUSHKA = registerNPC("Prushka", 16)

    init {
        registerTypes()
    }
}
