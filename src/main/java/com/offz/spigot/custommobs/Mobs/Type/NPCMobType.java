package com.offz.spigot.custommobs.Mobs.Type;


import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.NPCBehaviour;
import com.offz.spigot.custommobs.Mobs.NPCs.Marulk;
import com.offz.spigot.custommobs.Mobs.NPCs.Mitty;
import com.offz.spigot.custommobs.Mobs.NPCs.Nanachi;
import com.offz.spigot.custommobs.Mobs.NPCs.Ozen;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

import java.util.function.Function;

public enum NPCMobType implements MobType {
    MITTY("Mitty", (short) 2,
            new NPCBehaviour(), Mitty.class, Mitty::new
    ),
    NANACHI("Nanachi", (short) 3,
            new NPCBehaviour(), Nanachi.class, Nanachi::new),
    MARULK("Marulk", (short) 8,
            new NPCBehaviour(), Marulk.class, Marulk::new),
    OZEN("Ozen", (short) 10,
            new NPCBehaviour(), Ozen.class, Ozen::new);

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;
    private final Material material;
    private final Class entityClass;
    private final Function<? super World, ? extends Entity> entityFromClass;

    NPCMobType(String name, short modelID, MobBehaviour behaviour, Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;
        this.entityClass = entityClass;
        this.entityFromClass = entityFromClass;
        this.material = Material.DIAMOND_AXE;
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
}
