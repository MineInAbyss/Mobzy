package com.offz.spigot.abyssalcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.PassiveMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.World;

public class Neritantan extends PassiveMob implements HitBehaviour {
    public Neritantan(World world) {
        super(world, "Neritantan");
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, getStaticBuilder().getTemptItems()));
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
