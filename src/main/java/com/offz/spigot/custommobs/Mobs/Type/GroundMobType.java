package com.offz.spigot.custommobs.Mobs.Type;


import com.offz.spigot.custommobs.Mobs.Behaviours.NeritantanBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;

public enum GroundMobType implements MobType {
    NERITANTAN("Neritantan", (short) 2,
            new NeritantanBehaviour()
    );

    private final String name;
    private final short modelID;
    private final MobBehaviour behaviour;

    GroundMobType(String name, short modelID, MobBehaviour behaviour) {
        this.name = name;
        this.modelID = modelID;
        this.behaviour = behaviour;

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
    public MobBehaviour getBehaviour() {
        return behaviour;
    }
}
