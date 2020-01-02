package com.offz.spigot.abyssalcreatures

import com.offz.spigot.abyssalcreatures.mobs.flying.*
import com.offz.spigot.abyssalcreatures.mobs.hostile.*
import com.offz.spigot.abyssalcreatures.mobs.passive.*
import com.offz.spigot.mobzy.CustomType

class AbyssalType : CustomType() {
    //(..., func = ::Neritantan)   is the same as   (...){ world -> Neritantan(world) }   or   (...){ Neritantan(it) }

    //Passive
    val ASHIMITE = registerEntity("ashimite", width = 2f, height = 2f, func = ::Ashimite)
    val FUWAGI = registerEntity("fuwagi", width = 0.6f, height = 0.6f, func = ::Fuwagi)
    val MAKIHIGE = registerEntity("makihige", width = 2f, height = 2f, func = ::Makihige)
    val NERITANTAN = registerEntity("neritantan", width = 0.6f, height = 0.6f, func = ::Neritantan)
    val OKIBO = registerEntity("okibo", width = 3f, height = 3f, func = ::Okibo)

    //Hostile
    val INBYO = registerEntity("inbyo", width = 0.6f, height = 3f, func = ::Inbyo)
    val KUONGATARI = registerEntity("kuongatari", width = 0.6f, height = 0.6f, func = ::Kuongatari)
    val OTTOBAS = registerEntity("ottobas", width = 2f, height = 3f, func = ::Ottobas)
    val SILKFANG = registerEntity("silkfang", width = 2f, height = 2f, func = ::Silkfang)
    val STEVE = registerEntity("steve", width = 2f, height = 7f, func = ::Steve)
    val TAMAUGACHI = registerEntity("tamaugachi", width = 2.5f, height = 2.5f, func = ::Tamaugachi)
    val TESUCHI = registerEntity("tesuchi", width = 0.6f, height = 0.6f, func = ::Tesuchi)
    //Flying
    val CORPSE_WEEPER = registerEntity("corpse_weeper", width = 3f, height = 3f, func = ::CorpseWeeper)
    val CYATORIA = registerEntity("cyatoria", width = 3f, height = 2f, func = ::Cyatoria)
    val DOSETORI = registerEntity("dosetori", width = 3f, height = 2f, func = ::Dosetori)
    val HAMMERBEAK = registerEntity("hammerbeak", width = 3f, height = 2f, func = ::Hammerbeak)
    val KAZURA = registerEntity("kazura", width = 1f, height = 1f, func = ::Kazura)
    val MADOKAJACK = registerEntity("madokajack", width = 4.5f, height = 3f, func = ::Madokajack)
    val ROHANA = registerEntity("rohana", width = 0.6f, height = 0.6f, func = ::Rohana)

    fun registerNPC(name: String, modelID: Int) = registerEntity(toEntityTypeID(name), "npc", width = 0.6f, height = 2f) { NPC(it, name, modelID) }
    //NPCs
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
}
