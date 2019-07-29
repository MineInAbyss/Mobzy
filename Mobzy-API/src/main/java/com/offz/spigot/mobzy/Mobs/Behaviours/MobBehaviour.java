package com.offz.spigot.mobzy.Mobs.Behaviours;

import com.offz.spigot.mobzy.Mobs.CustomMob;

public abstract class MobBehaviour {
    protected CustomMob mob;

    public MobBehaviour(CustomMob mob) {
        this.mob = mob;
    }
}
