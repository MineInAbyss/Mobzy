package com.offz.spigot.abyssalcreatures

import com.offz.spigot.abyssalcreatures.mobs.passive.Fuwagi
import com.offz.spigot.abyssalcreatures.mobs.passive.Neritantan
import com.offz.spigot.mobzy.CustomType
import net.minecraft.server.v1_15_R1.Entity
import net.minecraft.server.v1_15_R1.EntityTypes
import net.minecraft.server.v1_15_R1.World


class AbyssalType : CustomType() {
    //        val NERITANTAN = a("bat", EntityTypes.a.a(Neritantan::class.java, EnumCreatureType.CREATURE).a(0.5F, 0.9F))
//    val NERITANTAN = register<Entity>("neritantan", EntityTypes.a.a<Entity>({ _, world: World -> Neritantan(world) }, EnumCreatureType.MISC).c().a(6.0f, 0.5f))
    fun toB(func: (EntityTypes<*>, World) -> Entity) = EntityTypes.b(func)

    val NERITANTAN = registerEntity("neritantan", toB { type, world -> Neritantan(world) })
    val FUWAGI = registerEntity("fuwagi", toB { type, world -> Fuwagi(world) })

    //Passive
    //EntityTypes.a.a<Entity>(, EnumCreatureType.MISC).c().a(0.5f, 0.5f)
//    val OKIBO = registerEntity(Neritantan::class.java) { world: World? -> Okibo(world) }
    /*val FUWAGI = registerEntity(Fuwagi::class.java) { world: World? -> Fuwagi(world) }
    val ASHIMITE = registerEntity(Ashimite::class.java) { world: World? -> Ashimite(world) }
    val MAKIHIGE = registerEntity(Makihige::class.java) { world: World? -> Makihige(world) }
    //Hostile
    val INBYO = registerEntity(Inbyo::class.java) { world: World? -> Inbyo(world) }
    val ROHANA = registerEntity(Rohana::class.java) { world: World? -> Rohana(world) }
    val TAMAGAUCHI = registerEntity(Tamaugachi::class.java) { world: World? -> Tamaugachi(world) }
    val SILKFANG = registerEntity(Silkfang::class.java) { world: World? -> Silkfang(world) }
    val KUONGATARI = registerEntity(Kuongatari::class.java) { world: World? -> Kuongatari(world) }
    val TESUCHI = registerEntity(Tesuchi::class.java) { world: World? -> Tesuchi(world) }
    val OTTOBAS = registerEntity(Ottobas::class.java) { world: World? -> Ottobas(world) }
    val STEVE = registerEntity(Steve::class.java) { world: World? -> Steve(world) }
    //Flying
    val CORPSE_WEEPER = registerEntity(CorpseWeeper::class.java) { world: World? -> CorpseWeeper(world) }
    val MADOKAJACK = registerEntity(Madokajack::class.java) { world: World? -> Madokajack(world) }
    val HAMMERBEAK = registerEntity(Hammerbeak::class.java) { world: World? -> Hammerbeak(world) }
    val KAZURA = registerEntity(Kazura::class.java) { world: World? -> Kazura(world) }
    val BENIKUCHINAWA = registerEntity(Benikuchinawa::class.java) { world: World? -> Benikuchinawa(world) }
    val DOSETORI = registerEntity(Dosetori::class.java) { world: World? -> Dosetori(world) }
    val CYATORIA = registerEntity(Cyatoria::class.java) { world: World? -> Cyatoria(world) }
    //NPCs
    val MITTY = registerEntity("mitty", NPC::class.java) { world: World? -> NPC(world, "Mitty", 2) }
    val NANACHI = registerEntity("nanachi", NPC::class.java) { world: World? -> NPC(world, "Nanachi", 3) }
    val BONDREWD = registerEntity("bondrewd", NPC::class.java) { world: World? -> NPC(world, "Bondrewd", 4) }
    val HABO = registerEntity("habo", NPC::class.java) { world: World? -> NPC(world, "Habo", 5) }
    val JIRUO = registerEntity("jiruo", NPC::class.java) { world: World? -> NPC(world, "Jiruo", 6) }
    val KIYUI = registerEntity("kiyui", NPC::class.java) { world: World? -> NPC(world, "Kiyui", 7) }
    val MARULK = registerEntity("marulk", NPC::class.java) { world: World? -> NPC(world, "Marulk", 8) }
    val NAT = registerEntity("nat", NPC::class.java) { world: World? -> NPC(world, "Nat", 9) }
    val OZEN = registerEntity("ozen", NPC::class.java) { world: World? -> NPC(world, "Ozen", 10) }
    val REG = registerEntity("reg", NPC::class.java) { world: World? -> NPC(world, "Reg", 11) }
    val RIKO = registerEntity("riko", NPC::class.java) { world: World? -> NPC(world, "Riko", 12) }
    val SHIGGY = registerEntity("shiggy", NPC::class.java) { world: World? -> NPC(world, "Shiggy", 13) }
    val TORKA = registerEntity("torka", NPC::class.java) { world: World? -> NPC(world, "Torka", 14) }
    val LYZA = registerEntity("lyza", NPC::class.java) { world: World? -> NPC(world, "Lyza", 15) }
    val PRUSHKA = registerEntity("prushka", NPC::class.java) { world: World? -> NPC(world, "Prushka", 16) }*/
}
