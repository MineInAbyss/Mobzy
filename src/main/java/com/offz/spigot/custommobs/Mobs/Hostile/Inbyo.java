package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.IAttributeBuilder;
import com.offz.spigot.custommobs.Mobs.CustomZombie;
import com.offz.spigot.custommobs.Mobs.Passive.PassiveMob;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;

public class Inbyo extends CustomZombie {
    public Inbyo(World world) {
        super(world);
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, PassiveMob.class, true));
        setInitAttributes(this, new IAttributeBuilder().setMaxHealth(40.0).setMovementSpeed(0.45).setAttackDamage(7.0).setFollowRange(64.0));
    }
}
