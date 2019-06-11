package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class NPC extends PassiveMob {

    public NPC(World world, String name, int modelID) {
        super(world, new MobBuilder(name, modelID)
                .setDisguiseAs(DisguiseType.VILLAGER)
                .setModelMaterial(Material.DIAMOND_AXE));

        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
    }

    @Override
    protected void createPathfinders() {
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F));
    }

    //TODO get this method to work in the abstract class
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}