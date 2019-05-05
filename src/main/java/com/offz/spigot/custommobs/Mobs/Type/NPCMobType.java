/*
package com.offz.spigot.custommobs.Mobs.Type;


import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.NPCBehaviour;
import com.offz.spigot.custommobs.Mobs.Passive.NPC;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

import java.util.function.Function;

public enum NPCMobType implements MobType {
    MITTY("Mitty", (short) 2,
            new NPCBehaviour(), NPC.class, NPC::new, true),
    NANACHI("Nanachi", (short) 3,
            new NPCBehaviour(), NPC.class, NPC::new),
    BONDREWD("Bondrewd", (short) 4,
            new NPCBehaviour(), NPC.class, NPC::new),
    HABO("Habo", (short) 5,
            new NPCBehaviour(), NPC.class, NPC::new),
    JIRUO("Jiruo", (short) 6,
            new NPCBehaviour(), NPC.class, NPC::new),
    KIYUI("Kyui", (short) 7,
            new NPCBehaviour(), NPC.class, NPC::new, true),
    MARULK("Marulk", (short) 8,
            new NPCBehaviour(), NPC.class, NPC::new),
    NAT("Nat", (short) 9,
            new NPCBehaviour(), NPC.class, NPC::new),
    OZEN("Ozen", (short) 10,
            new NPCBehaviour(), NPC.class, NPC::new),
    REG("Reg", (short) 11,
            new NPCBehaviour(), NPC.class, NPC::new),
    RIKO("Riko", (short) 12,
            new NPCBehaviour(), NPC.class, NPC::new),
    SHIGGY("Shiggy", (short) 13,
            new NPCBehaviour(), NPC.class, NPC::new),
    TORKA("Torka", (short) 14,
            new NPCBehaviour(), NPC.class, NPC::new);

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;
    private final Material material;
    private final Class entityClass;
    private final Function<? super World, ? extends Entity> entityFromClass;
    private final boolean isBaby;

    NPCMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        this.material = Material.DIAMOND_AXE;
        this.isBaby = false;
        behaviour.setMobType(this);
    }

    NPCMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        this.material = Material.DIAMOND_AXE;
        this.isBaby = isBaby;
        behaviour.setMobType(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getModelID() {
        return modelID;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Class getEntityClass() {
        return entityClass;
    }

    @Override
    public Function<? super World, ? extends Entity> getEntityFromClass() {
        return entityFromClass;
    }

    @Override
    public MobBehaviour getBehaviour() {
        return behaviour;
    }

    public boolean isBaby() {
        return isBaby;
    }
}*/
