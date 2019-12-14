package com.offz.spigot.mobzy.mobs.behaviours;

import com.offz.spigot.mobzy.mobs.CustomMob;

public abstract class MobBehaviour {
    protected CustomMob mob;

    public MobBehaviour(CustomMob mob) {
        this.mob = mob;
    }
}
