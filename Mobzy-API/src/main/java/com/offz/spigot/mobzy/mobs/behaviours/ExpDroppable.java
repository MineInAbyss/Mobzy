package com.offz.spigot.mobzy.mobs.behaviours;

import com.offz.spigot.mobzy.mobs.MobTemplate;
import com.offz.spigot.mobzy.mobs.CustomMob;

import java.util.Random;

public class ExpDroppable extends MobBehaviour {
    public ExpDroppable(CustomMob mob) {
        super(mob);
    }

    public Integer getExpToDrop() {
        MobTemplate builder = mob.getBuilder();
        if(builder.getMinExp() == null || builder.getMaxExp() == null)
            return null;

        return builder.getMinExp() + new Random().nextInt(builder.getMaxExp() - builder.getMinExp());
    }
}
