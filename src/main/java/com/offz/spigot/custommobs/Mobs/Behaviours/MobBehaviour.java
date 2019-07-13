package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.CustomMob;

public abstract class MobBehaviour {
    protected CustomMob mob;

    public MobBehaviour(CustomMob mob) {
        this.mob = mob;
    }
}
