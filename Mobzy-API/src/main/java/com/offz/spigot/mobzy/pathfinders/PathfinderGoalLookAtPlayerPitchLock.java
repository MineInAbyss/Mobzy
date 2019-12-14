package com.offz.spigot.mobzy.pathfinders;

import com.offz.spigot.mobzy.mobs.CustomMob;
import com.offz.spigot.mobzy.MobzyAPI;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.entity.LivingEntity;

import java.util.Random;

public class PathfinderGoalLookAtPlayerPitchLock extends PathfinderGoal {
    private final float startChance; //know for sure
    protected CustomMob mob; //know for sure
    protected LivingEntity entity;
    protected EntityInsentient nmsEntity;
    protected Entity lookAt; //know for sure
    protected float radius;
    protected EntityTypes<?> targetType;
    private int length;
    private Random random = new Random();

    public PathfinderGoalLookAtPlayerPitchLock(CustomMob mob, EntityTypes<?> targetType, float radius) {
        this(mob, targetType, radius, 0.02F);
    }

    public PathfinderGoalLookAtPlayerPitchLock(CustomMob mob, EntityTypes<?> targetType, float radius, float startChance) {
        this.mob = mob;
        entity = ((LivingEntity) mob.getEntity().getBukkitEntity());
        nmsEntity = ((EntityInsentient) mob.getEntity());
        this.targetType = targetType;
        this.radius = radius;
        this.startChance = startChance;
    }

    public boolean a() {
        if (random.nextFloat() >= this.startChance) {
            return false;
        } else {
            if (nmsEntity.getGoalTarget() != null)
                lookAt = nmsEntity.getGoalTarget();

            entity.getNearbyEntities(radius, radius, radius).stream()
                    .filter(other -> MobzyAPI.getEntityType(MobzyAPI.toNMS(other)) == targetType)
                    .min((one, two) -> mob.distanceToEntity(one) < mob.distanceToEntity(two) ? -1 : 1)
                    .ifPresent(other -> lookAt = MobzyAPI.toNMS(other));

            return this.lookAt != null;
        }
    }

    public boolean b() {
        if (!lookAt.isAlive()
                || this.mob.distanceToEntity(lookAt.getBukkitEntity()) > (double) (this.radius * this.radius))
            return false;
        else
            return this.length > 0;
    }

    public void c() {
        length = 40 + random.nextInt(40);
    }

    public void d() {
        lookAt = null;
    }

    public void e() {
        mob.lookAt(lookAt);
        --length;
    }
}
