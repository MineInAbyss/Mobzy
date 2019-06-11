package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.Passive.Neritantan;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Inbyo extends HostileMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Inbyo", 8)
            .setDrops(Arrays.asList(new ItemStack(Material.BEEF), new ItemStack(Material.BLACK_WOOL)));

    public Inbyo(World world) {
        super(world, builder);
        this.setSize(0.6F, 2.5F);
    }

    @Override
    protected void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, Neritantan.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.45D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0);
    }
}