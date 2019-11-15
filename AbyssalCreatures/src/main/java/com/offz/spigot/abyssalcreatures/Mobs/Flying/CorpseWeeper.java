package com.offz.spigot.abyssalcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import net.minecraft.server.v1_13_R2.World;

public class CorpseWeeper extends FlyingMob implements HitBehaviour {
    public CorpseWeeper(World world) {
        super(world, "Corpse Weeper");
        setSize(3f, 3f);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(2, new PathfinderGoalDiveOnTargetAttack(this, -0.3, 6, 10, 8, 2, 0.6, 30));
    }

    @Override
    public String soundAmbient() {
        return randomSound("entity.corpseweeper.snarl2");
    }

    @Override
    public String soundDeath() {
        return "entity.corpseweeper.snarl2";
    }

    @Override
    public String soundHurt() {
        return "entity.corpseweeper.snarl2";
    }
}