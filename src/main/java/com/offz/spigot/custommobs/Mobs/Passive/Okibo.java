package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Okibo extends PassiveMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Okibo", 38)
            .setDrops(new MobDrop(Material.LEATHER, 3, 5));

    //TODO change offset when riding and make controllable
    public Okibo(World world) {
        super(world, builder);
        setSize(3, 3);
    }

    @Override
    public void onRightClick(EntityHuman player) {
        player.startRiding(this);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(50.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Okibo(this.world);
    }
}
