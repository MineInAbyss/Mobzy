package com.offz.spigot.custommobs.MobType;

import com.offz.spigot.custommobs.Behaviours.NeritantanBehaviour;
import com.offz.spigot.custommobs.Mobs.MobBehaviour;


    public enum StandardMobType implements MobType {
        NERITANTAN("Neritantan",
                new NeritantanBehaviour()
        );

        private final String name;
        private final MobBehaviour behaviour;

        StandardMobType(String name, MobBehaviour behaviour) {
            this.name = name;
            this.behaviour = behaviour;

            behaviour.setMobType(this);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MobBehaviour getBehaviour() {
            return behaviour;
        }

    }