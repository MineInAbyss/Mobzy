package com.offz.spigot.custommobs.Pathfinders;

import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.util.Vector;

public class PathfinderGoalWalkingAnimation extends PathfinderGoal {
    private final EntityCreature mob;
    private final int modelID;

    public PathfinderGoalWalkingAnimation(EntityCreature entitycreature, int modelID) {
        this.mob = entitycreature;
        this.modelID = modelID;
    }

    public boolean a() {
        ItemStack model = this.mob.getEquipment(EnumItemSlot.HEAD);
        if (model.getDamage() == modelID + 2) //if showing hit model
            return false;
        Vector v = mob.getBukkitEntity().getVelocity();


        if (v.getX() == 0 && v.getZ() == 0)
            model.setDamage(modelID);
        else
            model.setDamage(modelID + 1);
        mob.setEquipment(EnumItemSlot.HEAD, model);
        return true; //if this returns true, I think any less important behaviours don't get to run
    }
}