package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Types.HostileMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Kuongatari extends HostileMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Kuongatari", 35)
            .setDrops(new MobDrop(Material.LIME_DYE, 1, 2));

    public Kuongatari(World world) {
        super(world, builder);
        this.setSize(0.6F, 0.6F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.6F));
    }
}