package com.offz.spigot.abyssalcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import net.minecraft.server.v1_13_R2.World;

public class Benikuchinawa extends FlyingMob implements HitBehaviour {
    public Benikuchinawa(World world) {
        super(world, "Benikuchinawa");
        setSize(4.5f, 2f);
        //An alternative method for setting boundaries which doesn't seem to work anymore
//        AxisAlignedBB boundingBox = new AxisAlignedBB(locX - 5, locY, locZ - 1, locX + 5, locY + 1, locZ + 1);
//        a(boundingBox);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(2, new PathfinderGoalDiveOnTargetAttack(this, -0.03, 3, 5, 8, 2, 0.6, 30));
    }

    @Override
    public String soundAmbient() {
        return "entity.llama.ambient";
    }

    @Override
    public String soundDeath() {
        return "entity.llama.death";
    }

    @Override
    public String soundHurt() {
        return "entity.llama.hurt";
    }
}