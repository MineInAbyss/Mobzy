package com.offz.spigot.custommobs.Behaviours.Task;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveAnimationTask extends BukkitRunnable {
    @Override
    public void run() {
        for (AnimationBehaviour.MobInfo mobInfo : AnimationBehaviour.registeredMobs.values()) {
            ((AnimationBehaviour) mobInfo.mobType.getBehaviour()).animate(mobInfo);
        }
    }
}