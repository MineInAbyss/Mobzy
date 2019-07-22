package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalFlyTowardsTarget;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalIdleFlyAboveGround;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.PathfinderGoalTargetNearestPlayer;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Rohana extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Rohana", 14)
            .setAdult(false)
            .setDrops(new MobDrop(Material.GLOWSTONE_DUST, 2));

    public Rohana(World world) {
        super(world, builder);
        this.setSize(0.6F, 0.6F);
    }

    @Override
    public void createPathfinders() {
        this.goalSelector.a(3, new PathfinderGoalFlyTowardsTarget(this));
        this.goalSelector.a(7, new PathfinderGoalIdleFlyAboveGround(this, 2, 5));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));

        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }
}