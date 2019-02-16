package com.offz.spigot.custommobs.Mobs;

import net.minecraft.server.v1_13_R2.*;

public class Neritantan extends PassiveMob {
    public Neritantan(World world) {
        super(world);
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(new IMaterial[]{Items.CARROT, Items.APPLE}), false));
    }
}
