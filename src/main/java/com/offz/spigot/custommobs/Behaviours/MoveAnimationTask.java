package com.offz.spigot.custommobs.Behaviours;

import org.bukkit.scheduler.BukkitRunnable;

public class MoveAnimationTask extends BukkitRunnable {
    @Override
    public void run() {
        for (WalkingMobBehaviour.MobInfo mobInfo : WalkingMobBehaviour.registeredMobs.values()) {
            ((WalkingMobBehaviour) mobInfo.mobType.getBehaviour()).animate(mobInfo);
        }
    }
}