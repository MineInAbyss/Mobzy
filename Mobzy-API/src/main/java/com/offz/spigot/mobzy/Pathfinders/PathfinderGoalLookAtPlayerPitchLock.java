package com.offz.spigot.mobzy.Pathfinders;

import net.minecraft.server.v1_13_R2.*;

public class PathfinderGoalLookAtPlayerPitchLock extends PathfinderGoal {
    protected EntityInsentient entity; //know for sure
    protected Entity lookAt; //know for sure
    protected float c;
    private int e;
    private final float startChance; //know for sure
    protected Class<? extends Entity> d;

    public PathfinderGoalLookAtPlayerPitchLock(EntityInsentient var1, Class<? extends Entity> var2, float var3) {
        this(var1, var2, var3, 0.02F);
    }

    public PathfinderGoalLookAtPlayerPitchLock(EntityInsentient var1, Class<? extends Entity> var2, float var3, float var4) {
        this.entity = var1;
        this.d = var2;
        this.c = var3;
        this.startChance = var4;
        this.a(2);
    }

    public boolean a() {
        if (this.entity.getRandom().nextFloat() >= this.startChance) {
            return false;
        } else {
            if (this.entity.getGoalTarget() != null) {
                this.lookAt = this.entity.getGoalTarget();
            }

            if (this.d == EntityHuman.class) {
                this.lookAt = this.entity.world.a(this.entity.locX, this.entity.locY, this.entity.locZ, (double)this.c, IEntitySelector.f.and(IEntitySelector.b(this.entity)));
            } else {
                this.lookAt = this.entity.world.a(this.d, this.entity.getBoundingBox().grow((double)this.c, 3.0D, (double)this.c), this.entity);
            }

            return this.lookAt != null;
        }
    }

    public boolean b() {
        if (!this.lookAt.isAlive()) {
            return false;
        } else if (this.entity.h(this.lookAt) > (double)(this.c * this.c)) {
            return false;
        } else {
            return this.e > 0;
        }
    }

    public void c() {
        this.e = 40 + this.entity.getRandom().nextInt(40);
    }

    public void d() {
        this.lookAt = null;
    }

    public void e() {
//        this.entity.getControllerLook().a(this.lookAt.locX, this.lookAt.locY + (double)this.lookAt.getHeadHeight(), this.lookAt.locZ, (float)this.entity.L(), (float)this.entity.K());
        this.entity.getControllerLook().a(this.lookAt.locX, this.entity.locY + (double)this.entity.getHeadHeight(), this.lookAt.locZ, (float)this.entity.L(), (float)this.entity.K());
        --this.e;
    }
}
