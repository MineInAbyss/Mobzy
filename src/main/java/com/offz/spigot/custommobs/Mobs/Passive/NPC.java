package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.*;

public class NPC extends PassiveMob {
    private static final Item[] temptItems = new Item[]{Items.CARROT, Items.POTATO, Items.BEETROOT};
    private static final String name = "NPC";
    private static final int modelID = 0;
    private static DisguiseType disguiseAs = DisguiseType.VILLAGER;

    public NPC(World world, String name, int modelID) {
        super(world, name, modelID);
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
    }

    public NPC(World world) {
        super(world, name, modelID);
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
    }

    @Override
    protected void createPathfinders(){
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F));
    }

    //TODO eventually this'll just be a getBuilder and we'll pass along the builder into super() for the constructor
    @Override
    DisguiseType getDisguiseType() {
        return disguiseAs;
    }

    //TODO get this method to work in the abstract class
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
