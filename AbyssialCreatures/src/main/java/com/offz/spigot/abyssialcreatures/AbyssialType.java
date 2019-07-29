package com.offz.spigot.abyssialcreatures;

import com.offz.spigot.abyssialcreatures.Mobs.Flying.*;
import com.offz.spigot.abyssialcreatures.Mobs.Hostile.*;
import com.offz.spigot.abyssialcreatures.Mobs.Passive.Fuwagi;
import com.offz.spigot.abyssialcreatures.Mobs.Passive.NPC;
import com.offz.spigot.abyssialcreatures.Mobs.Passive.Neritantan;
import com.offz.spigot.abyssialcreatures.Mobs.Passive.Okibo;
import com.offz.spigot.mobzy.CustomType;
import net.minecraft.server.v1_13_R2.EntityTypes;

@SuppressWarnings("SpellCheckingInspection")
public class AbyssialType extends CustomType {
    //Passive
    public static final EntityTypes NERITANTAN = registerEntity(Neritantan.class, Neritantan::new);
    public static final EntityTypes OKIBO = registerEntity(Okibo.class, Okibo::new);
    //Hostile
    public static final EntityTypes INBYO = registerEntity(Inbyo.class, Inbyo::new);
    public static final EntityTypes ROHANA = registerEntity(Rohana.class, Rohana::new);
    public static final EntityTypes TAMAGAUCHI = registerEntity(Tamaugachi.class, Tamaugachi::new);
    public static final EntityTypes SILKFANG = registerEntity(Silkfang.class, Silkfang::new);
    public static final EntityTypes KUONGATARI = registerEntity(Kuongatari.class, Kuongatari::new);
    public static final EntityTypes TESUCHI = registerEntity(Tesuchi.class, Tesuchi::new);
    public static final EntityTypes OTTOBAS = registerEntity(Ottobas.class, Ottobas::new);
    //Flying
    public static final EntityTypes CORPSE_WEEPER = registerEntity(CorpseWeeper.class, CorpseWeeper::new);
    public static final EntityTypes MADOKAJACK = registerEntity(Madokajack.class, Madokajack::new);
    public static final EntityTypes HAMMERBEAK = registerEntity(Hammerbeak.class, Hammerbeak::new);
    public static final EntityTypes KAZURA = registerEntity(Kazura.class, Kazura::new);
    public static final EntityTypes BENIKUCHINAWA = registerEntity(Benikuchinawa.class, Benikuchinawa::new);
    //NPCs
    public static final EntityTypes MITTY = registerEntity("mitty", NPC.class, (world -> new NPC(world, "Mitty", 2)));
    public static final EntityTypes NANACHI = registerEntity("nanachi", NPC.class, (world -> new NPC(world, "Nanachi", 3)));
    public static final EntityTypes BONDREWD = registerEntity("bondrewd", NPC.class, (world -> new NPC(world, "Bondrewd", 4)));
    public static final EntityTypes HABO = registerEntity("habo", NPC.class, (world -> new NPC(world, "Habo", 5)));
    public static final EntityTypes JIRUO = registerEntity("jiruo", NPC.class, (world -> new NPC(world, "Jiruo", 6)));
    public static final EntityTypes KIYUI = registerEntity("kiyui", NPC.class, (world -> new NPC(world, "Kiyui", 7)));
    public static final EntityTypes MARULK = registerEntity("marulk", NPC.class, (world -> new NPC(world, "Marulk", 8)));
    public static final EntityTypes NAT = registerEntity("nat", NPC.class, (world -> new NPC(world, "Nat", 9)));
    public static final EntityTypes OZEN = registerEntity("ozen", NPC.class, (world -> new NPC(world, "Ozen", 10)));
    public static final EntityTypes REG = registerEntity("reg", NPC.class, (world -> new NPC(world, "Reg", 11)));
    public static final EntityTypes RIKO = registerEntity("riko", NPC.class, (world -> new NPC(world, "Riko", 12)));
    public static final EntityTypes SHIGGY = registerEntity("shiggy", NPC.class, (world -> new NPC(world, "Shiggy", 13)));
    public static final EntityTypes TORKA = registerEntity("torka", NPC.class, (world -> new NPC(world, "Torka", 14)));
    public static final EntityTypes LYZA = registerEntity("lyza", NPC.class, (world -> new NPC(world, "Lyza", 15)));
    public static final EntityTypes PRUSHKA = registerEntity("prushka", NPC.class, (world -> new NPC(world, "Prushka", 16)));
    public static final EntityTypes FUWAGI = registerEntity(Fuwagi.class, Fuwagi::new);
}

