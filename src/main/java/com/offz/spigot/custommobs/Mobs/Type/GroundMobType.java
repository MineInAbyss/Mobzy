/*
package com.offz.spigot.custommobs.Mobs.Type;

import com.offz.spigot.custommobs.Behaviours.DeathBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.LivingMobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobDrops;
import com.offz.spigot.custommobs.Mobs.Behaviours.SingleEntityMobBehaviour;
import com.offz.spigot.custommobs.Mobs.CustomZombie;
import com.offz.spigot.custommobs.Mobs.Hostile.Inbyo;
import com.offz.spigot.custommobs.Mobs.Passive.Fuwagi;
import com.offz.spigot.custommobs.Mobs.Passive.Neritantan;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public enum GroundMobType implements MobType {


    //TODO So are we getting rid of this? it's used in a couple other places to get the class and check whether a custom
    // mob is in fact a custom mob, but i'm sure we could replace those things with something else
    NERITANTAN("Neritantan", (short) 2, new LivingMobBehaviour(), Neritantan.class, Neritantan::new, true),
    FUWAGI("Fuwagi", (short) 5, new LivingMobBehaviour(), Fuwagi.class, Fuwagi::new, true, new ArrayList<MobDrops>() {{
        add(new MobDrops(Material.PORKCHOP, "Raw Fuwagi", Arrays.asList(ChatColor.GRAY + "The fluffiest meal of the day"), 50));
    }}),
    INBYO("Inbyo", (short) 8, new LivingMobBehaviour(), Inbyo.class, Inbyo::new, false),
    ROHANA("Rohana", (short) 14, new SingleEntityMobBehaviour(), CustomZombie.class, CustomZombie::new, true, new ArrayList<MobDrops>() {{
        add(new MobDrops(Material.ROTTEN_FLESH, "Rohana flesh", 30));
        add(new MobDrops(Material.GLOWSTONE_DUST, 30));
    }});

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;
    private final Material material;
    private final Class entityClass;
    private final Function<? super World, ? extends Entity> entityFromClass;
    private final boolean isBaby;

    //TODO Make modelID int and just cast (short) inside this method
    GroundMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        this.isBaby = isBaby;
        this.material = Material.DIAMOND_SWORD;
        behaviour.setMobType(this);
    }

    GroundMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby, ArrayList<MobDrops> itemDrops) {
        this(name, modelID, behaviour, entityClass, entityFromClass, isBaby);
        //this isn't set when the mob itself is created, but per mob type so it makes sense for it to belong here
        //TODO: is there entity better place to create these in?
        if (behaviour instanceof DeathBehaviour) {
            DeathBehaviour.setItemDrops(this, itemDrops);
        }

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

    //TODO: Probably add this to the actual MobType interface
    public boolean isBaby() {
        return isBaby;
    }

}
*/
