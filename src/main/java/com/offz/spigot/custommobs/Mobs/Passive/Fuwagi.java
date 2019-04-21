package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.*;

public class Fuwagi extends PassiveMob {
    private static final Item[] temptItems = new Item[]{Items.CARROT, Items.APPLE};
    private static final String name = "Fuwagi";
    private static final int modelID = 5;
    private static DisguiseType disguiseAs = DisguiseType.SKELETON;

    public Fuwagi(World world) {
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
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2D);
    }

    DisguiseType getDisguiseType() {
        return disguiseAs;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return new MinecraftKey("entities/pig");
    }

    @Override
    protected SoundEffect soundHurt() {
        return SoundEffects.ENTITY_GENERIC_EXPLODE;
    }

    //creating children
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
