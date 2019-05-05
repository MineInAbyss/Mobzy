/*
package com.offz.spigot.custommobs.Mobs.Type;


import com.offz.spigot.custommobs.Mobs.Behaviours.LivingMobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.CorpseWeeper;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum FlyingMobType implements MobType {
    CORPSE_WEEPER("Corpse Weeper", (short) 11,
            new LivingMobBehaviour(), CorpseWeeper.class, CorpseWeeper::new, false, null
    );

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;
    private final Material material;
    private final Class entityClass;
    private final Function<? super World, ? extends Entity> entityFromClass;
    private final Map<String, Double> initAttributes = new HashMap<>();

    FlyingMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass, boolean isBaby, Map<String, Double> initAttributes) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        if (initAttributes != null)
            this.initAttributes.putAll(initAttributes);
        this.material = Material.DIAMOND_SWORD;

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

    public Map<String, Double> getInitAttributes() {
        return initAttributes;
    }
}*/
