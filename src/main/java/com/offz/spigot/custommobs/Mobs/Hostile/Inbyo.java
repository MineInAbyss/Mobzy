package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Passive.Neritantan;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Inbyo extends HostileMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Inbyo", 8)
            .setDrops(new MobDrop(Material.BEEF, 1, 2), new MobDrop(Material.BLACK_WOOL, 1, 3));

    public Inbyo(World world) {
        super(world, builder);
        this.setSize(0.6F, 3F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, Neritantan.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.42D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0);
    }
}