package com.offz.spigot.custommobs.Pathfinders;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class PathfinderGoalTemptPitchLock extends PathfinderGoal {
    private final EntityCreature a;
    private final double b;
    private final Item[] targetItems;
    private final boolean l;
    private double c;
    private double d;
    private double e;
    private double f;
    private double g;
    private EntityLiving target;
    private int i;
    private boolean j;

    public PathfinderGoalTemptPitchLock(EntityCreature entitycreature, double d0, Item[] items, boolean flag) {
        this(entitycreature, d0, flag, items);
    }

    public PathfinderGoalTemptPitchLock(EntityCreature entitycreature, double d0, boolean flag, Item[] items) {
        this.a = entitycreature;
        this.b = d0;
        this.targetItems = items;
        this.l = flag;
        this.a(3);
        if (!(entitycreature.getNavigation() instanceof Navigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    public boolean a() {
        if (this.i > 0) {
            --this.i;
            return false;
        } else {
            this.target = this.a.world.findNearbyPlayer(this.a, 10.0D);
            boolean tempt = false;
            if (this.target != null)
                for (Item item : this.targetItems)
                    if (this.target.getItemInMainHand().getItem().equals(item)) {
                        tempt = true;
                        break;
                    }
//                    tempt = true = this.target == null ? false : this.k.choices(this.target.getItemInMainHand()) || this.a(this.target.getItemInOffHand());
            if (tempt) {
                EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.a, this.target, EntityTargetEvent.TargetReason.TEMPT);
                if (event.isCancelled()) {
                    return false;
                }

                this.target = event.getTarget() == null ? null : ((CraftLivingEntity) event.getTarget()).getHandle();
            }

            return tempt;
        }
    }

    public void e() {
        this.a.getControllerLook().a(this.target.locX, this.a.locY + (double) this.a.getHeadHeight(), this.target.locZ, (float) this.a.L(), (float) this.a.K());
//            this.a.getControllerLook().a(this.target, (float)(this.a.L() + 20), (float)this.a.K());
        if (this.a.h(this.target) < 6.25D) {
            this.a.getNavigation().q();
        } else {
            this.a.getNavigation().a(this.target, this.b);
        }

    }
}
