package com.offz.spigot.mobzy.Mobs.Behaviours;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.Mobs.CustomMob;

import java.util.Random;

public class ExpDroppable extends MobBehaviour {
    public ExpDroppable(CustomMob mob) {
        super(mob);
    }

    public Integer getExpToDrop() {
        MobBuilder builder = mob.getBuilder();
        if(builder.getMinExp() == null || builder.getMaxExp() == null)
            return null;

        return builder.getMinExp() + new Random().nextInt(builder.getMaxExp() - builder.getMinExp());
    }
}
