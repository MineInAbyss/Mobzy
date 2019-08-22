package com.offz.spigot.mobzy.Pathfinders.Flying;

import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class PathfinderGoalDiveOnTargetAttack extends PathfinderGoal {
    private final FlyingMob mob;
    private final LivingEntity entity;
    private double diveVelocity;
    private Action currentAction = Action.FLY;
    private double minHeight;
    private double maxHeight;
    private double diveHeight;
    private double bashDuration = 30;
    private double bashLeft = bashDuration;
    private double bashVelX;
    private double bashVelZ;
    private double bashVelMultiplier = 0.6;
    private double startDiveHeightRange = 2;
    private double startDiveDistance = 8;

    public PathfinderGoalDiveOnTargetAttack(FlyingMob flying, double diveVelocity, double minHeight, double maxHeight, double startDiveDistance, double startDiveHeightRange, double bashVelMultiplier, double bashDuration) {
        this.mob = flying;
        this.diveVelocity = diveVelocity;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.startDiveDistance = startDiveDistance;
        this.startDiveHeightRange = startDiveHeightRange;
        this.bashVelMultiplier = bashVelMultiplier;
        this.bashDuration = bashDuration;
        diveHeight = pickDiveHeight();

        entity = ((LivingEntity) flying.getBukkitEntity());
    }

    public boolean a() {
        if (this.mob.getGoalTarget() == null) {
//            this.mob.yaw = -((float) MathHelper.c(this.mob.motX, this.mob.motZ)) * 57.295776F;
//            this.mob.aQ = this.mob.yaw;
            currentAction = Action.FLY;
            return false;
        }

        if (currentAction == Action.FLY)
            prepareDive();
        else if (currentAction == Action.DIVE)
            beginDive();
        else if (currentAction == Action.BASH)
            bash();
        return true;
    }

    public void prepareDive() {
        LivingEntity target = (LivingEntity) this.mob.getGoalTarget().getBukkitEntity();
        mob.lookAt(target);
        //if arrived to dive
        Location diveTarget = target.getLocation().clone().add(0, diveHeight, 0);
        if (mob.getLocation().distance(diveTarget) < startDiveDistance && Math.abs(mob.getLocation().getY() - diveTarget.getY()) < startDiveHeightRange) {
            diveHeight = pickDiveHeight();
            currentAction = Action.DIVE;
            return;
        }
        Location targetLoc = target.getLocation();
        mob.lookAt(target);

        mob.getControllerMove().a(targetLoc.getX(), targetLoc.getY() + diveHeight, targetLoc.getZ(), 1.0D);
    }

    public void beginDive() {
        LivingEntity target = (LivingEntity) this.mob.getGoalTarget().getBukkitEntity();
        mob.lookAt(target);
        Location targetLoc = target.getLocation();
        if (mob.distanceToEntity(target) < 2 || entity.getVelocity().getY() == 0 || mob.getY() < target.getLocation().getY() - 2) {
            currentAction = Action.BASH;
            bashVelX = entity.getLocation().getDirection().getX() * bashVelMultiplier;
            bashVelZ = entity.getLocation().getDirection().getZ() * bashVelMultiplier;
            return;
        }

        mob.getControllerMove().a(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 0.1D);
        entity.setVelocity(entity.getVelocity().setY(-Math.abs(diveVelocity)));

    }

    public void bash() {
        LivingEntity target = (LivingEntity) this.mob.getGoalTarget().getBukkitEntity();
        Location targetLoc = target.getLocation();
        if (bashLeft <= 0 || mob.distanceToEntity(target) < 2) {
            currentAction = Action.FLY;
            bashLeft = bashDuration;
            return;
        }

        mob.getControllerMove().a(targetLoc.getX(), mob.getY(), targetLoc.getZ(), 0.01D);

        entity.setVelocity(entity.getVelocity().setX(bashVelX).setZ(bashVelZ));
//        mob.lookAt(target);

        bashLeft--;
    }

    private double pickDiveHeight() {
        return Math.random() * (maxHeight - minHeight) + minHeight;
    }

    private enum Action {
        FLY, DIVE, BASH
    }
}