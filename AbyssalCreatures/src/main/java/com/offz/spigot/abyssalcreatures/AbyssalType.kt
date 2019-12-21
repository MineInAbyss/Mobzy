package com.offz.spigot.abyssalcreatures

import com.offz.spigot.abyssalcreatures.mobs.hostile.Inbyo
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
//    val ROHANA = registerEntity(Rohana::class.java) { world: World? -> Rohana(world) }
//    val TAMAGAUCHI = registerEntity(Tamaugachi::class.java) { world: World? -> Tamaugachi(world) }
//    val SILKFANG = registerEntity(Silkfang::class.java) { world: World? -> Silkfang(world) }
//    val KUONGATARI = registerEntity(Kuongatari::class.java) { world: World? -> Kuongatari(world) }
//    val TESUCHI = registerEntity(Tesuchi::class.java) { world: World? -> Tesuchi(world) }
//    val OTTOBAS = registerEntity(Ottobas::class.java) { world: World? -> Ottobas(world) }
//    val STEVE = registerEntity(Steve::class.java) { world: World? -> Steve(world) }
    //Flying
//    val CORPSE_WEEPER = registerEntity(CorpseWeeper::class.java) { world: World? -> CorpseWeeper(world) }
//    val MADOKAJACK = registerEntity(Madokajack::class.java) { world: World? -> Madokajack(world) }
//    val HAMMERBEAK = registerEntity(Hammerbeak::class.java) { world: World? -> Hammerbeak(world) }
//    val KAZURA = registerEntity(Kazura::class.java) { world: World? -> Kazura(world) }
//    val BENIKUCHINAWA = registerEntity(Benikuchinawa::class.java) { world: World? -> Benikuchinawa(world) }
//    val DOSETORI = registerEntity(Dosetori::class.java) { world: World? -> Dosetori(world) }
//    val CYATORIA = registerEntity(Cyatoria::class.java) { world: World? -> Cyatoria(world) }

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
