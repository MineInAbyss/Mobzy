package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Types.PassiveMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EnumMoveType;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class NPC extends PassiveMob {

    public NPC(World world, String name, int modelID) {
        super(world, new MobBuilder(name, modelID)
                .setModelMaterial(Material.DIAMOND_AXE));

        getBukkitEntity().setCustomName(name);
        setCustomNameVisible(true);
        setInvulnerable(true);
        setSize(0.6f, 0.6f);
        addScoreboardTag("npc");
    }

    //Stop from being pushed around
    @Override
    public void collide(Entity entity) {
    }

    @Override
    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
    }

    @Override
    public void createPathfinders() {
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F, 1F));
    }
}