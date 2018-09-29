package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.MobType.MobType;

public interface MobBehaviour {
        default void setMobType(MobType type){}
    }
