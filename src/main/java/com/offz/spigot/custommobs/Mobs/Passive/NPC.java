package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Material;

public class NPC extends PassiveMob {

    public NPC(World world, String name, int modelID) {
        super(world, new MobBuilder(name, modelID)
                .setModelMaterial(Material.DIAMOND_AXE));

        setCustomNameVisible(true);
        setInvulnerable(true);
        setSize(0.6f, 0.6f);
    }

    //Stop from being pushed around
    @Override
    public void collide(Entity entity) {
    }

    @Override
    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
    }

    @Override
    protected void createPathfinders() {
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F, 1F));
    }

    //TODO get this method to work in the abstract class
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}