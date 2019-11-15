package com.offz.spigot.abyssalcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public class NPC extends PassiveMob {

    public NPC(World world, String name, int modelID) {
        super(world, new MobBuilder(name, modelID)
                .setModelMaterial(Material.DIAMOND_AXE));

        getBukkitEntity().setCustomName(name);
        setCustomNameVisible(true);
        setInvulnerable(true);
        setSize(0.6f, 0.6f);
        addScoreboardTag("npc");
        ((LivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(false);
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
        goalSelector.a(2, new PathfinderGoalRandomLookaround(this));
        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 6.0F, 0.02F));
    }
}