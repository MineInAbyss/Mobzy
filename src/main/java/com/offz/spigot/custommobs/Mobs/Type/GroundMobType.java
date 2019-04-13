package com.offz.spigot.custommobs.Mobs.Type;

import com.offz.spigot.custommobs.Behaviours.DeathBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.LivingMobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobDrops;
import com.offz.spigot.custommobs.Mobs.Behaviours.SingleEntityMobBehaviour;
import com.offz.spigot.custommobs.Mobs.CustomZombie;
import com.offz.spigot.custommobs.Mobs.Inbyo;
import com.offz.spigot.custommobs.Mobs.Neritantan;
import com.offz.spigot.custommobs.Mobs.PassiveMob;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public enum GroundMobType implements MobType {
    NERITANTAN("Neritantan", (short) 2,
            new LivingMobBehaviour(), Neritantan.class, Neritantan::new, true, new HashMap<String, Double>() {{
        put("maxHealth", 10.0);
        put("MOVEMENT_SPEED", 0.3);
    }}
    ),FUWAGI("Fuwagi", (short) 5,
            new LivingMobBehaviour(), PassiveMob.class, PassiveMob::new, true, new HashMap<String, Double>() {{
        put("maxHealth", 10.0);
        put("MOVEMENT_SPEED", 0.2);
    }}, new ArrayList<MobDrops>() {{
        add(new MobDrops(Material.PORKCHOP, "Raw Fuwagi", Arrays.asList(ChatColor.GRAY + "The fluffiest meal of the day"), 50));
    }}
    ),INBYO("Inbyo", (short) 8,
            new LivingMobBehaviour(), Inbyo.class, Inbyo::new, false, new HashMap<String, Double>() {{
        put("maxHealth", 40.0);
        put("MOVEMENT_SPEED", 0.45);
        put("ATTACK_DAMAGE", 7.0);
        put("FOLLOW_RANGE", 64.0);
    }}
    ),ROHANA("Rohana", (short) 14,
            new SingleEntityMobBehaviour(), CustomZombie.class, CustomZombie::new, true, new HashMap<String, Double>() {{
        put("maxHealth", 5.0);
        put("MOVEMENT_SPEED", 0.3);
        put("ATTACK_DAMAGE", 0.2);
    }}, new ArrayList<MobDrops>() {{
        add(new MobDrops(Material.ROTTEN_FLESH, "Rohana flesh", 30));
        add(new MobDrops(Material.GLOWSTONE_DUST, 30));
    }}
    );

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;
    private final Material material;
    private final Class entityClass;
    private final Function<? super World, ? extends Entity> entityFromClass;
    private final boolean isBaby;
    private final Map<String, Double> initAttributes = new HashMap<>();

    //TODO Make modelID int and just cast (short) inside this method
    GroundMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby, Map<String, Double> initAttributes) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        this.isBaby = isBaby;
        if (initAttributes != null)
            this.initAttributes.putAll(initAttributes);
        this.material = Material.DIAMOND_SWORD;

        behaviour.setMobType(this);
    }

    GroundMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby, Map<String, Double> initAttributes, ArrayList<MobDrops> itemDrops) {
        this(name, modelID, behaviour, entityClass, entityFromClass, isBaby, initAttributes);
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

    //Also probably add to MobType
    public Map<String, Double> getInitAttributes() {
        return initAttributes;
    }

    //TODO: Probably add this to the actual MobType interface
    public boolean isBaby() {
        return isBaby;
    }

}
