package com.offz.spigot.custommobs.Pathfinders;

import net.minecraft.server.v1_13_R2.*;

public class PathfinderGoalMeleeAttackPitchLock extends PathfinderGoal {
    protected final int c = 20;
    private final double d;
    private final boolean e;
    protected EntityCreature a;
    protected int b;
    private PathEntity f;
    private int g;
    private double h;
    private double i;
    private double j;

    public PathfinderGoalMeleeAttackPitchLock(EntityCreature var0, double var1, boolean var3) {
        this.a = var0;
        this.d = var1;
        this.e = var3;
        this.a(3);
    }

    public boolean a() {
        EntityLiving var0 = this.a.getGoalTarget();
        if (var0 == null) {
            return false;
        } else if (!var0.isAlive()) {
            return false;
        } else {
            this.f = this.a.getNavigation().a(var0);
            if (this.f != null) {
                return true;
            } else {
                return this.a(var0) >= this.a.d(var0.locX, var0.getBoundingBox().minY, var0.locZ);
            }
        }
    }

    public boolean b() {
        EntityLiving var0 = this.a.getGoalTarget();
        if (var0 == null) {
            return false;
        } else if (!var0.isAlive()) {
            return false;
        } else if (!this.e) {
            return !this.a.getNavigation().p();
        } else if (!this.a.f(new BlockPosition(var0))) {
            return false;
        } else {
            return !(var0 instanceof EntityHuman) || !((EntityHuman) var0).isSpectator() && !((EntityHuman) var0).u();
        }
    }

    public void c() {
        this.a.getNavigation().a(this.f, this.d);
        this.g = 0;
    }

    public void d() {
        EntityLiving var0 = this.a.getGoalTarget();
        if (var0 instanceof EntityHuman && (((EntityHuman) var0).isSpectator() || ((EntityHuman) var0).u())) {
            this.a.setGoalTarget(null);
        }

        this.a.getNavigation().q();
    }

    public void e() {
        EntityLiving var0 = this.a.getGoalTarget();
//            this.a.getControllerLook().a(var0, 30.0F, 30.0F);
        double var1 = this.a.d(var0.locX, var0.getBoundingBox().minY, var0.locZ);
        --this.g;
        if ((this.e || this.a.getEntitySenses().a(var0)) && this.g <= 0 && (this.h == 0.0D && this.i == 0.0D && this.j == 0.0D || var0.d(this.h, this.i, this.j) >= 1.0D || this.a.getRandom().nextFloat() < 0.05F)) {
            this.h = var0.locX;
            this.i = var0.getBoundingBox().minY;
            this.j = var0.locZ;
            this.g = 4 + this.a.getRandom().nextInt(7);
            if (var1 > 1024.0D) {
                this.g += 10;
            } else if (var1 > 256.0D) {
                this.g += 5;
            }

            if (!this.a.getNavigation().a(var0, this.d)) {
                this.g += 15;
            }
        }

        this.b = Math.max(this.b - 1, 0);
        this.a(var0, var1);
    }

    protected void a(EntityLiving var0, double var1) {
        double var3 = this.a(var0);
        if (var1 <= var3 && this.b <= 0) {
            this.b = 20;
            this.a.a(EnumHand.MAIN_HAND);
            this.a.B(var0);
        }

    }

    protected double a(EntityLiving var0) {
        return (double) (this.a.width * 1.0F * this.a.width * 1.0F + var0.width);
    }
}
