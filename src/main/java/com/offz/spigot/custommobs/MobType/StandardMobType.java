package com.offz.spigot.custommobs.MobType;

import com.offz.spigot.custommobs.Behaviours.Mobs.NeritantanBehaviour;
import com.offz.spigot.custommobs.Mobs.MobBehaviour;


    public enum StandardMobType implements MobType {
        NERITANTAN("Neritantan", (short) 2,
                new NeritantanBehaviour()
        );

        private final String name;
        private final short modelID;
        private final MobBehaviour behaviour;

        StandardMobType(String name, short modelID, MobBehaviour behaviour) {
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