package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.Ghast;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class CorpseWeeper extends EntityGhast {


    public CorpseWeeper(World world) {
        super(world);
        Ghast corpse_weeper = (Ghast) this.getBukkitEntity();

        this.addScoreboardTag("customMob");
        corpse_weeper.setCustomName("Corpse Weeper");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        corpse_weeper.setRemoveWhenFarAway(true);


        corpse_weeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(corpse_weeper);
        AnimationBehaviour.registerMob(corpse_weeper, type, type.getModelID());

        this.getWorld().addEntity(this);
    }


    protected void n() {
        this.goalSelector.a(5, new PathfinderGoalGhastIdleMove(this));
        this.goalSelector.a(2, new PathfinderGoalGhastMoveTowardsTarget(this));
        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 200.0F));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(2);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    }

    static class PathfinderGoalGhastIdleMove extends PathfinderGoal {
        private final EntityGhast a;

        public PathfinderGoalGhastIdleMove(EntityGhast entityghast) {
            this.a = entityghast;
            this.a(1);
        }

        public boolean a() {
            ControllerMove controllermove = this.a.getControllerMove();
            if (!controllermove.b()) {
                return true;
            } else {
                double d0 = controllermove.d() - this.a.locX;
                double d1 = controllermove.e() - this.a.locY;
                double d2 = controllermove.f() - this.a.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean b() {
            return false;
        }

        public void c() {
            Random random = this.a.getRandom();
            double d0 = this.a.locX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.a.locY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.a.locZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.a.getControllerMove().a(d0, d1, d2, 1.0D);
        }
    }

    static class PathfinderGoalGhastMoveTowardsTarget extends PathfinderGoal {
        private final EntityGhast a;

        public PathfinderGoalGhastMoveTowardsTarget(EntityGhast entityghast) {
            this.a = entityghast;
            this.a(2);
        }

        public boolean a() {
            return true;
        }

        public void e() {
            if (this.a.getGoalTarget() == null) {
                this.a.yaw = -((float)MathHelper.c(this.a.motX, this.a.motZ)) * 100;
                this.a.aQ = this.a.yaw;
            } else {
                EntityLiving entityliving = this.a.getGoalTarget();
                if (entityliving.h(this.a) < 4096.0D) {
                    double d1 = entityliving.locX - this.a.locX;
                    double d2 = entityliving.locZ - this.a.locZ;
                    this.a.yaw = -((float)MathHelper.c(d1, d2)) * 100;
                    this.a.aQ = this.a.yaw;
                }
            }

        }
    }
}
