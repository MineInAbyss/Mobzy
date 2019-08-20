package com.offz.spigot.mobzy.Pathfinders.Flying;

import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.entity.LivingEntity;

public class PathfinderGoalFlyDamageTarget extends PathfinderGoal {
    private FlyingMob mob;
    private long hitAt;
    private long cooldown = 200;

    public PathfinderGoalFlyDamageTarget(FlyingMob flyingMob) {
        mob = flyingMob;
    }

    public boolean a() {
        return true;
    }

    public void e() {
        if (mob.getGoalTarget() != null && hitAt + cooldown < System.currentTimeMillis()) {
            LivingEntity target = (LivingEntity) mob.getGoalTarget().getBukkitEntity();
            Double attackDamage = mob.getStaticBuilder().getAttackDamage();
            //if within range, harm
            if (attackDamage != null && mob.distanceToEntity(target) < mob.width / 2 + 1) {
                hitAt = System.currentTimeMillis();
                target.damage(attackDamage.floatValue(), mob.getBukkitEntity());
            }
        }
    }
}