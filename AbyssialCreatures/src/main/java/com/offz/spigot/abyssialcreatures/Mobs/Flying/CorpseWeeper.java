package com.offz.spigot.abyssialcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Sound;

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
    public Sound soundAmbient() {
        return Sound.ENTITY_BAT_AMBIENT;
    }

    @Override
    public Sound soundDeath() {
        return Sound.ENTITY_BAT_DEATH;
    }

    @Override
    public Sound soundHurt() {
        return Sound.ENTITY_BAT_HURT;
    }
}