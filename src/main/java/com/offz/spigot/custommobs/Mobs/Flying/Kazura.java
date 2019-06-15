package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import net.minecraft.server.v1_13_R2.*;

public class Kazura extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Kazura", 29);

    public Kazura(World world) {
        super(world, builder);
        setSize(1f, 1f);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
    }

    @Override
    protected void createPathfinders() {
        this.goalSelector.a(7, new KaruzaMoveTowardsTarget(this));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));

        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }


    static class KaruzaMoveTowardsTarget extends PathfinderGoal {
        private final FlyingMob a;

        public KaruzaMoveTowardsTarget(FlyingMob entityghast) {
            this.a = entityghast;
            this.a(2);
        }

        public boolean a() {
            return true;
        }

        public void e() {
            if (this.a.getGoalTarget() == null) {
                this.a.yaw = -((float) MathHelper.c(this.a.motX, this.a.motZ)) * 57.295776F;
                this.a.aQ = this.a.yaw;
            } else {
                EntityLiving target = this.a.getGoalTarget();
                double d1 = target.locX - this.a.locX;
                double d2 = target.locZ - this.a.locZ;
                this.a.yaw = -((float) MathHelper.c(d1, d2)) * 57.295776F;
                this.a.aQ = this.a.yaw;

                this.a.getControllerMove().a(target.locX, target.locY, target.locZ, 4);
                if (target.locY > a.locY)
                    this.a.getControllerMove().a(a.locX, a.locY + 1, a.locZ, 4);

//                Bukkit.broadcastMessage(a.h(target) + "");
                if (a.h(target) < 1.8)
                    target.damageEntity(DamageSource.mobAttack(a), 1);
            }
        }
    }
}