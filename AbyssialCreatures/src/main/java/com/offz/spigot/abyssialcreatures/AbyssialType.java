package com.offz.spigot.abyssialcreatures;

import com.offz.spigot.abyssialcreatures.Mobs.Flying.*;
import com.offz.spigot.abyssialcreatures.Mobs.Hostile.*;
import com.offz.spigot.abyssialcreatures.Mobs.Passive.*;
import com.offz.spigot.mobzy.CustomType;
import net.minecraft.server.v1_13_R2.EntityTypes;

@SuppressWarnings("SpellCheckingInspection")
public class AbyssialType extends CustomType {
    //Passive
    public final EntityTypes NERITANTAN = registerEntity(Neritantan.class, Neritantan::new);
    public final EntityTypes OKIBO = registerEntity(Okibo.class, Okibo::new);
    public final EntityTypes FUWAGI = registerEntity(Fuwagi.class, Fuwagi::new);
    public final EntityTypes ASHIMITE = registerEntity(Ashimite.class, Ashimite::new);
    public final EntityTypes MAKIHIGE = registerEntity(Makihige.class, Makihige::new);
    //Hostile
    public final EntityTypes INBYO = registerEntity(Inbyo.class, Inbyo::new);
    public final EntityTypes ROHANA = registerEntity(Rohana.class, Rohana::new);
    public final EntityTypes TAMAGAUCHI = registerEntity(Tamaugachi.class, Tamaugachi::new);
    public final EntityTypes SILKFANG = registerEntity(Silkfang.class, Silkfang::new);
    public final EntityTypes KUONGATARI = registerEntity(Kuongatari.class, Kuongatari::new);
    public final EntityTypes TESUCHI = registerEntity(Tesuchi.class, Tesuchi::new);
    public final EntityTypes OTTOBAS = registerEntity(Ottobas.class, Ottobas::new);
    public final EntityTypes STEVE = registerEntity(Steve.class, Steve::new);
    //Flying
    public final EntityTypes CORPSE_WEEPER = registerEntity(CorpseWeeper.class, CorpseWeeper::new);
    public final EntityTypes MADOKAJACK = registerEntity(Madokajack.class, Madokajack::new);
    public final EntityTypes HAMMERBEAK = registerEntity(Hammerbeak.class, Hammerbeak::new);
    public final EntityTypes KAZURA = registerEntity(Kazura.class, Kazura::new);
    public final EntityTypes BENIKUCHINAWA = registerEntity(Benikuchinawa.class, Benikuchinawa::new);
    public final EntityTypes DOSETORI = registerEntity(Dosetori.class, Dosetori::new);
    public final EntityTypes CYATORIA = registerEntity(Cyatoria.class, Cyatoria::new);

    //NPCs
    public final EntityTypes MITTY = registerEntity("mitty", NPC.class, (world -> new NPC(world, "Mitty", 2)));
    public final EntityTypes NANACHI = registerEntity("nanachi", NPC.class, (world -> new NPC(world, "Nanachi", 3)));
    public final EntityTypes BONDREWD = registerEntity("bondrewd", NPC.class, (world -> new NPC(world, "Bondrewd", 4)));
    public final EntityTypes HABO = registerEntity("habo", NPC.class, (world -> new NPC(world, "Habo", 5)));
    public final EntityTypes JIRUO = registerEntity("jiruo", NPC.class, (world -> new NPC(world, "Jiruo", 6)));
    public final EntityTypes KIYUI = registerEntity("kiyui", NPC.class, (world -> new NPC(world, "Kiyui", 7)));
    public final EntityTypes MARULK = registerEntity("marulk", NPC.class, (world -> new NPC(world, "Marulk", 8)));
    public final EntityTypes NAT = registerEntity("nat", NPC.class, (world -> new NPC(world, "Nat", 9)));
    public final EntityTypes OZEN = registerEntity("ozen", NPC.class, (world -> new NPC(world, "Ozen", 10)));
    public final EntityTypes REG = registerEntity("reg", NPC.class, (world -> new NPC(world, "Reg", 11)));
    public final EntityTypes RIKO = registerEntity("riko", NPC.class, (world -> new NPC(world, "Riko", 12)));
    public final EntityTypes SHIGGY = registerEntity("shiggy", NPC.class, (world -> new NPC(world, "Shiggy", 13)));
    public final EntityTypes TORKA = registerEntity("torka", NPC.class, (world -> new NPC(world, "Torka", 14)));
    public final EntityTypes LYZA = registerEntity("lyza", NPC.class, (world -> new NPC(world, "Lyza", 15)));
    public final EntityTypes PRUSHKA = registerEntity("prushka", NPC.class, (world -> new NPC(world, "Prushka", 16)));
}

