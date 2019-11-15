package com.offz.spigot.abyssalcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import net.minecraft.server.v1_13_R2.World;

public class Cyatoria extends FlyingMob implements HitBehaviour {
    public Cyatoria(World world) {
        super(world, "Cyatoria");
        setSize(3F, 2F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(2, new PathfinderGoalDiveOnTargetAttack(this, -0.3, 6, 10, 8, 2, 0.6, 30));
    }
}