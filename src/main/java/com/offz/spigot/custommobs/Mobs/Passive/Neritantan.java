package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.*;

public class Neritantan extends PassiveMob {
    private static final Item[] temptItems = new Item[]{Items.CARROT, Items.POTATO, Items.BEETROOT};
    private static final String name = "Neritantan";
    private static final int modelID = 2;
    private static DisguiseType disguiseAs = DisguiseType.ZOMBIE;

    public Neritantan(World world) {
        super(world, name, modelID);
    }

    @Override
    protected void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, modelID));
        this.goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, temptItems));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    DisguiseType getDisguiseType() {
        return disguiseAs;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return new MinecraftKey("entities/zombie");
    }

    //creating children
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
