package com.offz.spigot.custommobs.Behaviours.Task;

import com.offz.spigot.custommobs.Behaviours.WalkingBehaviour;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveAnimationTask extends BukkitRunnable {
    @Override
    public void run() {
        for (WalkingBehaviour.MobInfo mobInfo : WalkingBehaviour.registeredMobs.values()) {
            ((WalkingBehaviour) mobInfo.mobType.getBehaviour()).animate(mobInfo);
        }
    }
}