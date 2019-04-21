package com.offz.spigot.custommobs.Pathfinders;

import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.util.Vector;

public class PathfinderGoalWalkingAnimation extends PathfinderGoal {
    private final EntityCreature a;
    private final int modelID;

    public PathfinderGoalWalkingAnimation(EntityCreature entitycreature, int modelID) {
        this.a = entitycreature;
        this.modelID = modelID;
        this.a(3); //TODO figure out what i is for
    }

    public boolean a() {
        ItemStack model = this.a.getEquipment(EnumItemSlot.HEAD);
        Vector v = this.a.getBukkitEntity().getVelocity();
        //TODO this.a.velocityChanged exists in entity, but seems to do nothing, is it a better way of checking for movement?
        if (v.getX() == 0 && v.getZ() == 0)
            model.setDamage(modelID);
        else
            model.setDamage(modelID + 1);
        this.a.setEquipment(EnumItemSlot.HEAD, model);
        return false; //if this returns true, I think any less important behaviours don't get to run
    }
}
